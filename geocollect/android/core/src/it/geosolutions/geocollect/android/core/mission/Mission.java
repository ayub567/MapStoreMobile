/*
 * GeoSolutions - MapstoreMobile - GeoSpatial Framework on Android based devices
 * Copyright (C) 2014  GeoSolutions (www.geo-solutions.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geocollect.android.core.mission;

import it.geosolutions.android.map.wfs.geojson.feature.Feature;
import it.geosolutions.geocollect.android.core.GeoCollectApplication;
import it.geosolutions.geocollect.android.core.login.LoginActivity;
import it.geosolutions.geocollect.android.core.mission.utils.MissionUtils;
import it.geosolutions.geocollect.model.config.MissionTemplate;
import it.geosolutions.geocollect.model.viewmodel.Field;

import java.io.Serializable;
import java.util.List;

import jsqlite.Database;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * <Mission> Provides access to mission data, origins and configuration 
 * to get values 
 * @author Lorenzo Natali (lorenzo.natali@geo-solutions.it)
 *
 */
public class Mission implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ORIGIN_PROVIDER_TAG="origin";
	private static final String DATA_PROVIDER_TAG="data";
	private static final String CONFIG_PROVIDER_TAG="config";
	private static final String LOCAL_PROVIDER_TAG="local";
	
	private MissionTemplate template;
	private Feature origin;
	/**
	 * Reference of the SQLite/Spatialite Database
	 */
	transient public Database db;
	
	/**
	 * provides a <String> value for the <Field>. The <Field> class's value attribute
	 * can contain some rules to get data from a particular object using the syntax {datasource.name}
	 * The options for the datasource part are:
	 * config: the configuration of the template directly
	 * data: the data provided as origin of the mission
	 * local: information stored on the device, like username, phone details
	 * external: an external service
	 * @param field the field
	 * @return the value of the data as String
	 */
	public String getValueAsString(final Context context, Field field){
		String value = field.value;
		return getValueAsString(context, field,value);

	}

	public String getValueAsString(final Context context, Field f,String value) {
		List<String> tags = MissionUtils.getTags(value);
		//the tags are not present. the value is a pure string
		if(tags == null){
			return value;
		}
		for(String tag : tags){
			//get data and replace
			Object val =getValueByTag(context,tag);
			if(val!=null){
				value = value.replace("${"+tag+"}", val.toString());
			}else{
				//replace with empty string if missing
				value = value.replace("${"+tag+"}", ""); 
				/*note: the source.data format has a point(.)
				and it is a regex special char,but in not 
				a problem for now, because allowed values match the path in unuque way
				*/
			}
		}
		return value;
	}

	/**
	 * Parse a tag and find the proper value for the tag.
	 * @param tag the tag
	 * @return the value as <String>
	 */
	public Object getValueByTag(final Context context, String tag) {
		//separate the path with points
		String[] path = tag.split("\\.",2);
		//nothing to do if the path is not composed by 2 parts
		if(path.length<=1){
			return tag;
		}else{
			/*the provider can be data, local, external ...
			  It's used to understand where to get the value
			*/
			String provider = path[0];
			if(ORIGIN_PROVIDER_TAG.equals(provider)){
				return getOriginValue(path[1]);
			}else if(DATA_PROVIDER_TAG.equals(provider)){
				//TODO get it from the original data
				return getDataValue(path[1]);
			}else if(CONFIG_PROVIDER_TAG.equals(provider)){
				if(template != null && template.config!=null){
					return template.config.get(path[1]);
				}
			}
			else if(LOCAL_PROVIDER_TAG.equals(provider)){
				return getLocalValue(context, path[1]);
			}
			
		}
		return null;
	}

	/**
	 * Provides a value from local device configuration (TBD)
	 * (Username, local time)
	 * @param string
	 * @return
	 */
	private Object getLocalValue(final Context context,String string) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(string.equals("user_name")){
			return prefs.getString(LoginActivity.PREFS_USER_FORENAME, null);
		}else if(string.equals("user_surname")){
			return prefs.getString(LoginActivity.PREFS_USER_SURNAME, null);
		}else if(string.equals("user_organization")){
			return prefs.getString(LoginActivity.PREFS_USER_ENTE, null);
		}
		return null;
	}

	/**
	 * Provides a value from the current Data Value (TBD)
	 * @param string
	 * @return
	 */
	private Object getDataValue(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Provides a value from the Mission Origin record
	 * @param string
	 * @return
	 */
	private Object getOriginValue(String string) {
		if(origin!=null && string !=null){
				if(string.equals(origin.geometry_name)){
					return origin.geometry;
				}

				if (origin.properties != null){
					if(origin.properties.containsKey(string)){
						return origin.properties.get(string);
					}
				}
		}
		return null;
	}

	/**
	 * Return the <MissionTemplate>
	 * @return
	 */
	public MissionTemplate getTemplate() {
		return template;
	}

	public void setTemplate(MissionTemplate template) {
		this.template = template;
	}

	/**
	 * @param myFeature
	 */
	public void setOrigin(Feature origin) {
		this.origin = origin;
		
	}
	public Feature getOrigin(){
		return origin;
	}
	
}
