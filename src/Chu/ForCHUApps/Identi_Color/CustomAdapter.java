package Chu.ForCHUApps.Identi_Color;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CustomAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	
	public CustomAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public CustomAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		
		TextView colourName = (TextView) view.findViewById(R.id.colourNameTextView);
		colourName.setText(cursor.getString(cursor.getColumnIndex("colourName")));
		TextView hex = (TextView) view.findViewById(R.id.hexTextView);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.linlay);
		hex.setText(cursor.getString(cursor.getColumnIndex("hex")));
		layout.setBackgroundColor(cursor.getInt(cursor.getColumnIndex("colour")));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.log_item, parent, false);
	}

}
