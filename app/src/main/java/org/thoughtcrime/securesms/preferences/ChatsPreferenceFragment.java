package org.thoughtcrime.securesms.preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference; // JW: added

import org.greenrobot.eventbus.EventBus;
import org.thoughtcrime.securesms.ApplicationPreferencesActivity;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.devicetransfer.olddevice.OldDeviceTransferActivity;
import org.thoughtcrime.securesms.keyvalue.SignalStore;
import org.thoughtcrime.securesms.permissions.Permissions;
import org.thoughtcrime.securesms.storage.StorageSyncHelper;
import org.thoughtcrime.securesms.util.ConversationUtil;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.thoughtcrime.securesms.util.ThrottledDebouncer;

public class ChatsPreferenceFragment extends ListSummaryPreferenceFragment {
  private static final String PREFER_SYSTEM_CONTACT_PHOTOS = "pref_system_contact_photos";

  private final ThrottledDebouncer refreshDebouncer = new ThrottledDebouncer(500);

  @Override
  public void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);

    findPreference(TextSecurePreferences.MESSAGE_BODY_TEXT_SIZE_PREF)
        .setOnPreferenceChangeListener(new ListSummaryListener());
    findPreference(TextSecurePreferences.BACKUP_LOCATION_REMOVABLE_PREF) // JW: added
        .setOnPreferenceChangeListener(new BackupLocationListener());

    findPreference(TextSecurePreferences.BACKUP).setOnPreferenceClickListener(unused -> {
      goToBackupsPreferenceFragment();
      return true;
    });

    findPreference(TextSecurePreferences.TRANSFER).setOnPreferenceClickListener(unused -> {
      goToTransferAccount();
      return true;
    });

    findPreference(PREFER_SYSTEM_CONTACT_PHOTOS)
        .setOnPreferenceChangeListener((preference, newValue) -> {
          SignalStore.settings().setPreferSystemContactPhotos(newValue == Boolean.TRUE);
          refreshDebouncer.publish(ConversationUtil::refreshRecipientShortcuts);
          StorageSyncHelper.scheduleSyncForDataChange();
          return true;
        });

    initializeListSummary((ListPreference) findPreference(TextSecurePreferences.MESSAGE_BODY_TEXT_SIZE_PREF));
  }

  // JW: added
  private class BackupLocationListener implements Preference.OnPreferenceChangeListener {
    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
      // Set the new preference ourself before calling setBackupSummary() to make it use
      // the correct backup directory.
      TextSecurePreferences.setBackupLocationRemovable(getActivity(), (boolean)newValue);
      TextSecurePreferences.setBackupLocationChanged(getActivity(), true); // Required for BackupUtil.getAllBackupsNewestFirst()
      return true;
    }
  }

  @Override
  public void onCreateEncryptedPreferences(@Nullable Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.preferences_chats);
  }

  @Override
  public void onResume() {
    super.onResume();
    ((ApplicationPreferencesActivity)getActivity()).getSupportActionBar().setTitle(R.string.preferences_chats__chats);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }

  private void goToBackupsPreferenceFragment() {
    ((ApplicationPreferencesActivity) requireActivity()).pushFragment(new BackupsPreferenceFragment());
  }

  private void goToTransferAccount() {
    requireContext().startActivity(new Intent(requireContext(), OldDeviceTransferActivity.class));
  }

  public static CharSequence getSummary(Context context) {
    return null;
  }
}
