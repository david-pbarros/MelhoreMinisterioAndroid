package br.com.dbcorp.melhoreministerio.preferencias;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.widget.BaseAdapter;

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

    public static class ConfigFragment extends PreferenceFragment implements OnPreferenceChangeListener {
        private EditTextPreference prefCong;
        private RingtonePreference ringPref;
        private PreferenceScreen somScreen;
        private CheckBoxPreference chkSom;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            this.prefCong = (EditTextPreference) findPreference("nrCong");
            this.ringPref = (RingtonePreference) findPreference("alarm");
            this.somScreen = (PreferenceScreen) findPreference("cat_som");
            this.chkSom = (CheckBoxPreference) findPreference("som_pers");

            this.prefCong.setOnPreferenceChangeListener(this);
            this.ringPref.setOnPreferenceChangeListener(this);
            this.chkSom.setOnPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();

            this.setRingToneSumary(null);
            this.setScreenSomSumary(this.chkSom.isChecked());
            this.prefCong.setSummary(this.prefCong.getText());
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof RingtonePreference) {
                this.setRingToneSumary((String) newValue);

            } else if (preference.getKey().equals(this.chkSom.getKey())) {
                this.setScreenSomSumary((Boolean) newValue);
                ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();

            } else {
                preference.setSummary((String) newValue);
            }

            return true;
        }

        private void setRingToneSumary(String value) {
            value = value == null ? this.ringPref.getSharedPreferences().getString(this.ringPref.getKey(),"") : value;

            Ringtone ringtone = RingtoneManager.getRingtone(this.getActivity(), Uri.parse(value));

            this.ringPref.setSummary(ringtone.getTitle(this.getActivity()));
        }

        private void setScreenSomSumary(boolean checked) {
            if(checked) {
                somScreen.setSummary("Personalizado");
            } else {
                somScreen.setSummary("Padrão");
            }
        }
    }
}
