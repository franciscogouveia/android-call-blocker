package eu.de_gouveia.callblocker

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var roleManager: RoleManager
    private lateinit var statusText: TextView
    private lateinit var requestRoleButton: Button
    private lateinit var notificationStatusText: TextView
    private lateinit var requestNotificationsButton: Button
    private lateinit var blockingSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        roleManager = getSystemService(RoleManager::class.java)
        statusText = findViewById(R.id.status)
        requestRoleButton = findViewById(R.id.request_role)
        notificationStatusText = findViewById(R.id.notification_status)
        requestNotificationsButton = findViewById(R.id.request_notifications)
        blockingSwitch = findViewById(R.id.blocking_switch)
        requestRoleButton.setOnClickListener { requestCallScreeningRole() }
        requestNotificationsButton.setOnClickListener { requestNotificationAccess() }
        blockingSwitch.setOnCheckedChangeListener { _, enabled ->
            BlockingPreference.setEnabled(this, enabled)
        }
    }

    override fun onResume() {
        super.onResume()
        updateRoleStatus()
        updateNotificationStatus()
        blockingSwitch.isChecked = BlockingPreference.isEnabled(this)
    }

    @Suppress("DEPRECATION")
    private fun requestCallScreeningRole() {
        if (!roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) return
        startActivityForResult(
            roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),
            ROLE_REQUEST_CODE,
        )
    }

    @Deprecated("The platform role request API still uses activity results on API 29")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ROLE_REQUEST_CODE) updateRoleStatus()
    }

    private fun updateRoleStatus() {
        val available = roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)
        val state = roleState(
            isRoleAvailable = available,
            isRoleHeld = available && roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING),
        )
        statusText.setText(
            if (state == RoleState.ACTIVE) R.string.status_active else R.string.status_not_active,
        )
        requestRoleButton.visibility = if (state == RoleState.ACTIVE) View.GONE else View.VISIBLE
        requestRoleButton.isEnabled = available
        if (!available) requestRoleButton.setText(R.string.role_unavailable)
    }

    private fun requestNotificationAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_REQUEST_CODE)
            return
        }

        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName),
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_REQUEST_CODE) updateNotificationStatus()
    }

    private fun updateNotificationStatus() {
        val manager = getSystemService(NotificationManager::class.java)
        val enabled = manager.areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        notificationStatusText.setText(
            if (enabled) R.string.notifications_enabled else R.string.notifications_disabled,
        )
        requestNotificationsButton.setText(
            if (enabled) R.string.manage_notifications else R.string.enable_notifications,
        )
    }

    private companion object {
        const val ROLE_REQUEST_CODE = 1
        const val NOTIFICATION_REQUEST_CODE = 2
    }
}
