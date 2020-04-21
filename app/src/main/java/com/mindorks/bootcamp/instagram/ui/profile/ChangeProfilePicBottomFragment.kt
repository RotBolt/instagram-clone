package com.mindorks.bootcamp.instagram.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.ui.base.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.layout_change_profile_pic_bottom_sheet.view.*

class ChangeProfilePicBottomFragment : BaseBottomSheetDialog() {

    companion object{
        const val TAG = "ChangeProfilePicBottomFragment"
    }

    override fun provideLayoutResId(): Int = R.layout.layout_change_profile_pic_bottom_sheet
    override fun setUpView(itemView: View) {
        itemView.viewCamera.setOnClickListener(this)
        itemView.viewGallery.setOnClickListener(this)
    }
}