package com.jakode.contacts.utils.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.utils.Intents
import com.jakode.contacts.utils.manager.NavigateManager
import com.jakode.contacts.utils.manager.SelectionManager
import kotlinx.android.synthetic.main.bottom_delete_layout.view.*
import kotlinx.android.synthetic.main.bottom_share_layout.*
import kotlinx.android.synthetic.main.bottom_share_layout.view.*
import java.util.*

class BottomSheet(
    val type: Type,
    val activity: Activity,
    theme: Int,
    val userInfo: UserInfo? = null,
    val users: List<UserInfo>? = null,
    val selectionManager: SelectionManager? = null
) : BottomSheetDialog(activity, theme) {
    private var navController: NavigateManager = activity as NavigateManager
    private var appRepository: AppRepository = AppRepository(context)

    private var view: View = when (type) {
        Type.BOTTOM_SHARE -> LayoutInflater.from(context)
            .inflate(R.layout.bottom_share_layout, bottomSheetContainer)
        Type.BOTTOM_DELETE -> LayoutInflater.from(context)
            .inflate(R.layout.bottom_delete_layout, bottomSheetContainer)
        Type.BOTTOM_SELECT_TO_DELETE -> LayoutInflater.from(context)
            .inflate(R.layout.bottom_deletes_layout, bottomSheetContainer)
    }

    enum class Type {
        BOTTOM_SHARE, BOTTOM_SELECT_TO_DELETE, BOTTOM_DELETE
    }

    init {
        setContentView(view)
        onClick()
    }

    private fun onClick() {
        when (type) {
            Type.BOTTOM_SHARE -> {
                // Share with file
                view.file.setOnClickListener {
                    dismiss()
                    Intents.sendVCard(context, listOf(userInfo!!))
                }

                // Share with text
                view.text.setOnClickListener {
                    dismiss()
                    Intents.sendText(context, getUserText(userInfo!!))
                }
            }
            Type.BOTTOM_DELETE -> {
                // Cancel
                view.cancel.setOnClickListener {
                    dismiss()
                }

                // Delete
                view.move.setOnClickListener {
                    dismiss()
                    appRepository.deleteUser(userInfo!!.user.id.toString())
                    navController.navigateUp()
                }
            }
            Type.BOTTOM_SELECT_TO_DELETE -> {
                view.subtitle.text =
                    if (Locale.getDefault().language == "fa") {
                        "${users!!.size} ${activity.getString(R.string.deletes)}"
                    } else {
                        val text = activity.getString(R.string.deletes)
                        "${text.substring(0, 4)} ${users!!.size}${text.substring(4, text.length)}"
                    }

                // Cancel
                view.cancel.setOnClickListener {
                    dismiss()
                }

                // Delete
                view.move.setOnClickListener {
                    dismiss()
                    selectionManager!!.removeUsers(users) // Clear from recycler
                    if (selectionManager.getItemCount() == users.size) {
                        appRepository.deleteAllUsers()
                    } else {
                        appRepository.deleteUsers(users.map { it.user.id.toString() })
                    }
                    selectionManager.onContactAction(false)
                }
            }
        }
    }

    private fun getUserText(userInfo: UserInfo): String {
        val (firstName, lastName) = userInfo.user.name.split(";;")
        return "[${getName()}] $firstName $lastName\n" +
                getPhones(userInfo.phones) +
                getEmails(userInfo.emails) +
                if (userInfo.profile.birthday != null) {
                    "[${getBirthday()}] ${userInfo.profile.birthday}\n"
                } else {
                    ""
                } +
                if (userInfo.profile.address != null) {
                    "[${getAddress()}] ${userInfo.profile.address}\n"
                } else {
                    ""
                } +
                if (userInfo.profile.description != null) {
                    "[${getDescription()}] ${userInfo.profile.description}\n"
                } else {
                    ""
                }
    }

    private fun getPhones(phones: List<String>): String {
        val builder = StringBuilder()
        phones.forEach { builder.append("[${getPhone()}] $it\n") }
        return builder.toString()
    }

    private fun getEmails(emails: List<String>): String {
        val builder = StringBuilder()
        emails.forEach { builder.append("[${getEmail()}] $it\n") }
        return builder.toString()
    }

    @SuppressLint("ResourceType")
    private fun getName() = context.resources.getString(R.string.name)
    private fun getPhone() = context.resources.getString(R.string.phone)
    private fun getEmail() = context.resources.getString(R.string.email)
    private fun getBirthday() = context.resources.getString(R.string.birthday)
    private fun getAddress() = context.resources.getString(R.string.address)
    private fun getDescription() = context.resources.getString(R.string.description)
}