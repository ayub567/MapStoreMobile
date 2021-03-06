package it.geosolutions.geocollect.android.core.mission;

import it.geosolutions.geocollect.android.core.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for MissionFeatures "created missions"
 *
 */
public class CreatedMissionAdapter extends ArrayAdapter<MissionFeature>{

	private int resourceId;
	
	public CreatedMissionAdapter(Context context, int resource) {
		super(context, resource);
		
		this.resourceId = resource;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

	    // assign the view we are converting to a local variable
	    View v = convertView;

	    // first check to see if the view is null. if so, we have to inflate it.
	    // to inflate it basically means to render, or show, the view.
	    if (v == null) {
	        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(resourceId, null);
	    }

	    /*
	     * Recall that the variable position is sent in as an argument to this
	     * method. The variable simply refers to the position of the current object
	     * in the list. (The ArrayAdapter iterates through the list we sent it)
	     * Therefore, i refers to the current Item object.
	     */
	    MissionFeature mission = getItem(position);

	    if (mission != null) {

	    	// display name

	    	TextView name = (TextView) v.findViewById(R.id.created_mission_resource_name);
	    	if (name != null && mission.properties != null && mission.properties.containsKey("CODICE")) {
	    		Object prop =mission.properties.get("CODICE");
	    		if(prop!=null){
	    			name.setText(prop.toString());
	    		}else{
	    			name.setText("");
	    		}

	    	}

	    	//display rifiuti

	    	TextView desc = (TextView) v.findViewById(R.id.created_mission_resource_description);
	    	if (desc != null && mission.properties != null && mission.properties.containsKey("RIFIUTI_NO")) {
	    		Object prop =mission.properties.get("RIFIUTI_NO");
	    		if(prop!=null){
	    			desc.setText(prop.toString());
	    		}else{
	    			desc.setText("");
	    		}

	    	}
	    	
			ImageView priorityIcon = (ImageView) v.findViewById(R.id.created_mission_resource_priority_icon);
			if ( priorityIcon != null && priorityIcon.getDrawable() != null ){
				
				// Get the icon and tweak the color
				Drawable d = priorityIcon.getDrawable();
				
				if ( mission.displayColor != null ){
					try{
						d.mutate().setColorFilter(Color.parseColor(mission.displayColor), PorterDuff.Mode.SRC_ATOP);
					}catch(IllegalArgumentException iae){
						Log.e("MissionAdapter", "A feature has an incorrect color value" );
					}
		    	}else{
		    		d.mutate().clearColorFilter();
		    	}

	    	}

	    }
	    return v;

	}
	
}
