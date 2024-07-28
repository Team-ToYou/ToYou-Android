package com.toyou.toyouandroid.presentation.fragment.social

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import com.toyou.toyouandroid.databinding.DialogLayoutBinding

class CustomDialogFragment : DialogFragment() {

    private var _binding : DialogLayoutBinding? = null
    private val binding : DialogLayoutBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogLayoutBinding.inflate(inflater, container, false)

        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)


        return binding.root
    }

    fun initDialog(){
        binding.dialogTv.text = arguments?.getString("dialogTitle")
        val  btnBundle = arguments?.getStringArray("btnText")

        binding.cancleBtn.setOnClickListener {
            buttonClickListener.onButton1Clicked()
            dismiss()
        }

        binding.okBtn.setOnClickListener {
            buttonClickListener.onButton2Clicked()
            dismiss()
        }
    }

    interface OnButtonClickListener {
        fun onButton1Clicked()
        fun onButton2Clicked()
        
    }

    override fun onStart() {
        super.onStart();
        val lp : WindowManager.LayoutParams  =  WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        val window: Window = dialog!!.window!!
        window.attributes =lp
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener

}