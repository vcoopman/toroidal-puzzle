package com.curso.toroidal_puzzle.ui.help

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.curso.toroidal_puzzle.R

class CreditosFragment : Fragment() {

    companion object {
        fun newInstance() = CreditosFragment()
    }

    private lateinit var viewModel: CreditosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.creditos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreditosViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
