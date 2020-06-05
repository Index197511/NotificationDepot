package me.index197511.notificationdepot.ui.notificationlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.notification_list_fragment.*
import me.index197511.notificationdepot.databinding.NotificationListFragmentBinding
import me.index197511.notificationdepot.service.model.Notification
import me.index197511.notificationdepot.ui.notificationlist.decoration.SwipeTouchCallback
import me.index197511.notificationdepot.ui.notificationlist.notificationitem.NotificationListItem
import org.koin.android.viewmodel.ext.android.viewModel

class NotificationListFragment : Fragment() {
    private val viewModel by viewModel<NotificationListViewModel>()
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var binding: NotificationListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NotificationListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.notificationList.adapter = adapter
        ItemTouchHelper(touchCallback).attachToRecyclerView(binding.notificationList)

        viewModel.notifications.observe(viewLifecycleOwner, Observer {
            it?.let { updateNotificationList(it) }
        })

        binding.buttonNavToNotificationSettings.setOnClickListener {
            navToSettingNotificationListener()
        }

        binding.buttonRemoveAll.setOnClickListener {
            viewModel.removeAllNotification()
        }
    }

    override fun onResume() {
        viewModel.updateNotificationList()
        viewModel.switchObserverEnabled()
        super.onResume()
    }

    private fun updateNotificationList(notificationList: List<Notification>) {
        ItemTouchHelper(touchCallback).attachToRecyclerView(notification_list)
        adapter.update(mutableListOf<Group>().apply {
            notificationList.forEach { notification ->
                run {
                    add(NotificationListItem(notification))
                }
            }
        })
    }

    private fun navToSettingNotificationListener() {
        val intentToSettingNotificationListener =
            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        intentToSettingNotificationListener.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intentToSettingNotificationListener)
    }

    private val touchCallback: SwipeTouchCallback by lazy {
        object : SwipeTouchCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.removeSpecifyNotification(viewHolder.adapterPosition)
                adapter.apply {
                    notifyItemRemoved(viewHolder.adapterPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }


}
