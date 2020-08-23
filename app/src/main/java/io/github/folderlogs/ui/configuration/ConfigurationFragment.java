//    The GNU General Public License does not permit incorporating this program
//    into proprietary programs.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

package io.github.folderlogs.ui.configuration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.preference.PreferenceFragmentCompat;

import com.jaredrummler.cyanea.prefs.CyaneaSettingsActivity;
import com.pixplicity.easyprefs.library.Prefs;

import io.github.folderlogs.R;
import io.github.folderlogs.tools.Scheduler;
import io.github.folderlogs.ui.NumberPref;

import static android.content.Context.POWER_SERVICE;

public class ConfigurationFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.configuration, rootKey);
		findPreference("command theme configuration").setOnPreferenceClickListener(preference -> {
			getActivity().startActivity(new Intent(getActivity(), CyaneaSettingsActivity.class));
			return false;
		});
		new NumberPref().change(findPreference("auto scan minutes"));
		findPreference("auto scan").setOnPreferenceChangeListener((preference, newValue) -> {
			Prefs.putBoolean("auto scan", Boolean.parseBoolean(newValue.toString()));
			new Scheduler(getActivity()).scheduleScanService();
			if (Boolean.parseBoolean(newValue.toString())) {
				try {
					requestIgnoreBatteryOptimizations();
				} catch (NullPointerException n) {
					n.printStackTrace();
				}
			}
			return true;
		});
		findPreference("auto scan minutes").setOnPreferenceChangeListener((preference, newValue) -> {
			Prefs.putString("auto scan minutes", newValue.toString());
			new Scheduler(getActivity()).scheduleScanService();
			return true;
		});
	}

	@SuppressLint("BatteryLife")
	public void requestIgnoreBatteryOptimizations() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PowerManager pm = (PowerManager) requireActivity().getSystemService(POWER_SERVICE);
			if (!pm.isIgnoringBatteryOptimizations(requireActivity().getPackageName())) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
				requireActivity().startActivity(intent);
			}
		}
	}
}