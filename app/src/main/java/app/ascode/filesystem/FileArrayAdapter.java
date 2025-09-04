package app.ascode.filesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.io.File;


import android.widget.*;

import app.ascode.R;

public class FileArrayAdapter extends ArrayAdapter<File> {

    public FileArrayAdapter(Context context, ArrayList<File> files) {
        super(context, 0, files);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final File file = getItem(position);
		String name = file.getName();

        if (view == null) {
        	view = LayoutInflater.from(getContext())
			.inflate(R.layout.file_list_item, parent, false);
        }

		ImageView iv = view.findViewById(R.id.ic_iv);
		if(file.isDirectory()) iv.setImageResource(R.drawable.ic_folder);
		else iv.setImageResource(R.drawable.ic_files);

        TextView nameTV = view.findViewById(R.id.name_tv);
        nameTV.setText(name);

        return view;
    }
}
