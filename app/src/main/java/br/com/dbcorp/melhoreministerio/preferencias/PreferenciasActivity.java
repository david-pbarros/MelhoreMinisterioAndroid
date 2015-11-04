package br.com.dbcorp.melhoreministerio.preferencias;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;

import br.com.dbcorp.melhoreministerio.R;

public class PreferenciasActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ConfigFragment())
                .commit();

    }

    public static class ConfigFragment extends PreferenceFragment {
        EditTextPreference prefCong;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            ChanceListener listener = new ChanceListener();

            this.prefCong = (EditTextPreference)findPreference("nrCong");
            this.prefCong.setSummary(this.prefCong.getText());

            this.prefCong.setOnPreferenceChangeListener(listener);
        }
    }

    static class ChanceListener implements OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            preference.setSummary((String) newValue);

            return false;
        }
    }
}
