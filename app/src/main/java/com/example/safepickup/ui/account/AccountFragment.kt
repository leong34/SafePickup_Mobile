package com.example.safepickup.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.safepickup.R

class AccountFragment : Fragment() {
    private var accountViewModel: AccountViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_account, container, false)


//        final TextView textView = root.findViewById(R.id.text_account);
//        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        val listview_account = root.findViewById<ListView?>(R.id.list_account)
        val adapter = accountViewModel?.getListData(activity?.getBaseContext())
        listview_account?.setAdapter(adapter)
        listview_account?.setOnItemClickListener(OnItemClickListener { parent, view, position, id -> Toast.makeText(activity?.getBaseContext(), "" + adapter?.getItem(position), Toast.LENGTH_SHORT).show() })
        return root
    }
}