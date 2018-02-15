package accesscollective.uwastudentguild.com.accesscollective;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

public class FirebaseUpdateFragment extends Fragment {

    public interface UpdateCallbacks {
        void onPreExecute();
        void onPostExecute(Checkpoint[] checkpoints);
    }

    private UpdateCallbacks mCallbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (UpdateCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // TODO: Get campusName from SharedPreferences
        new UpdateTask().execute();
    }

    private class UpdateTask extends AsyncTask<Void, Void, Checkpoint[]> {

        private final String ERROR_CLASS_NAME = UpdateTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            } else {
                Log.e(ERROR_CLASS_NAME, "mCallbacks is null for onPreExecute()");
            }
        }

        @Override
        protected Checkpoint[] doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Checkpoint[] checkpoints) {
            super.onPostExecute(checkpoints);
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(checkpoints);
            } else {
                Log.e(ERROR_CLASS_NAME, "mCallbacks is null for onPostExecute()");
            }
        }
    }
}
