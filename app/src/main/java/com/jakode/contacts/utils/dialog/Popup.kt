package com.jakode.contacts.utils.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.utils.manager.SelectionManager
import com.jakode.contacts.utils.ButtonBox
import kotlinx.android.synthetic.main.main_popup_layout.view.*
import kotlinx.android.synthetic.main.selection_popup_layout.view.*
import kotlinx.android.synthetic.main.show_user_popup_layout.view.*

object PopupMenu {
    private lateinit var type: Type
    private lateinit var view: View

    private var userInfo: UserInfo? = null
    private var selectionManager: SelectionManager? = null
    private var buttonBox: ButtonBox? = null

    enum class Type {
        MAIN_POPUP, SELECTION_MODE_POPUP, SHOW_USER_POPUP
    }

    // PopupWindow display method
    @SuppressLint("InflateParams")
    fun show(
        type: Type,
        userInfo: UserInfo?,
        view: View,
        x: Int,
        y: Int,
        selectionManager: SelectionManager?,
        buttonBox: ButtonBox?
    ) {
        // Initialize
        initialize(type, userInfo, view, selectionManager, buttonBox)

        // Create a View object yourself through inflater
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = when (type) {
            Type.MAIN_POPUP -> inflater.inflate(R.layout.main_popup_layout, null)
            Type.SHOW_USER_POPUP -> inflater.inflate(R.layout.show_user_popup_layout, null)
            Type.SELECTION_MODE_POPUP -> inflater.inflate(R.layout.selection_popup_layout, null)
        }

        // Specify the length and width through constants
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        // Make Inactive Items Outside Of PopupWindow
        val focusable = true

        // Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.elevation = 5F

        // Set the location of the window on the screen
        popupWindow.showAsDropDown(view, x, y)

        // Initialize the elements of our window, install the handler
        clickListener(popupView, popupWindow)
    }

    private fun clickListener(popupView: View, popupWindow: PopupWindow) {
        when (type) {
            Type.MAIN_POPUP -> {
                popupView.delete.setOnClickListener {
                    popupWindow.dismiss()
                    if (selectionManager!!.getItemCount() != 0) {
                        selectionManager!!.onContactAction(true)
                        buttonBox!!.hideShareButton()
                    } else {
                        Toast.makeText(view.context, view.context.getString(R.string.add_first), Toast.LENGTH_SHORT).show()
                    }
                }

                popupView.share.setOnClickListener {
                    popupWindow.dismiss()
                    if (selectionManager!!.getItemCount()  != 0) {
                        selectionManager!!.onContactAction(true)
                        buttonBox!!.hideDeleteButton()
                    } else {
                        Toast.makeText(view.context, view.context.getString(R.string.add_first), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Type.SHOW_USER_POPUP -> {
                popupView.delete_user.setOnClickListener {
                    popupWindow.dismiss()
                    BottomSheet(
                        BottomSheet.Type.BOTTOM_DELETE,
                        view.context as Activity,
                        R.style.BottomSheetDialogTheme,
                        userInfo!!
                    ).show()
                }

                popupView.block.setOnClickListener {
                    popupWindow.dismiss()
                    Toast.makeText(view.context, "block user", Toast.LENGTH_SHORT).show()
                }
            }
            Type.SELECTION_MODE_POPUP -> {
                popupView.create_group.setOnClickListener {
                    popupWindow.dismiss()
                    Toast.makeText(view.context, "create group", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initialize(
        type: Type,
        userInfo: UserInfo?,
        view: View,
        selectionManager: SelectionManager?,
        buttonBox: ButtonBox?
    ) {
        this.type = type
        this.userInfo = userInfo
        this.view = view
        this.selectionManager = selectionManager
        this.buttonBox = buttonBox
    }
}