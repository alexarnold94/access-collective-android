package accesscollective.uwastudentguild.com.accesscollective;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class filterMarkersDialogFragment extends DialogFragment {

    public ArrayList mSelectedItems;
    public ArrayList<String> selectedLayers;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList();
        selectedLayers = new ArrayList(); // Where we track selected layers

        final String[] layersToList = getArguments().getStringArray("LAYERS");
        //ArrayList<String> list2 = new ArrayList<>();
        //list2.add("test");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.filterMarkersDialogTitle)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(layersToList, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                    selectedLayers.add(layersToList[which]);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                    // TEST THIS
                                    selectedLayers.remove(which);
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        /*Intent i = new Intent()
                                .putStringArrayListExtra("SELECTED_LAYERS", mSelectedItems);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                        dismiss();*/
                        Intent i = new Intent()
                                .putStringArrayListExtra("SELECTED_LAYERS", selectedLayers);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                        dismiss();
                    }
                });
                /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });*/

        return builder.create();
    }
}