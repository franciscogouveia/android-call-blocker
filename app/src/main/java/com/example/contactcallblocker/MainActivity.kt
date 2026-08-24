package com.example.contactcallblocker

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var roleManager: RoleManager
    private lateinit var statusText: TextView
    private lateinit var requestRoleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        roleManager = getSystemService(RoleManager::class.java)
        statusText = findViewById(R.id.status)
        requestRoleButton = findViewById(R.id.request_role)
        requestRoleButton.setOnClickListener { requestCallScreeningRole() }
    }

    override fun onResume() {
        super.onResume()
        updateRoleStatus()
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

    private companion object {
        const val ROLE_REQUEST_CODE = 1
    }
}
