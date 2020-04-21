package com.mindorks.bootcamp.instagram.ui.base

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindorks.bootcamp.instagram.R

abstract class BaseBottomSheetDialog : BottomSheetDialogFragment(), View.OnClickListener {

    @LayoutRes
    abstract fun provideLayoutResId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(provideLayoutResId(), container, false)
    }

    override fun getTheme(): Int = R.style.AppModalStyle

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView(view)
    }

    abstract fun setUpView(itemView: View)

    private var itemClickListener: OnItemClickListener? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is OnItemClickListener) {
            itemClickListener = context as OnItemClickListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        itemClickListener = null
    }

    override fun onClick(v: View?) {
        v?.run {
            itemClickListener?.onItemClick(id)
            dismiss()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(@IdRes id: Int)
    }
}