package org.torproject.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import net.freehaven.tor.control.TorControlCommands
import org.torproject.android.service.OrbotConstants
import org.torproject.android.service.OrbotService
import org.torproject.android.service.util.Prefs
import org.torproject.android.ui.AppManagerActivity
import org.torproject.android.ui.OrbotMenuAction
import org.torproject.android.ui.OrbotMenuActionAdapter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConnectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConnectFragment : Fragment(), ConnectionHelperCallbacks, ExitNodeDialogFragment.ExitNodeSelectedCallback {


    // main screen UI
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvConfigure: TextView
    private lateinit var btnStartVpn: Button
    private lateinit var ivOnion: ImageView
    private lateinit var ivOnionShadow: ImageView
    public lateinit var progressBar: ProgressBar
    private lateinit var lvConnectedActions: ListView

    private var lastStatus: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        (activity as OrbotActivity).fragConnect = this
        lastStatus = (activity as OrbotActivity).previousReceivedTorStatus

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connect, container, false)
        view?.let {

            tvTitle = it.findViewById(R.id.tvTitle)
            tvSubtitle = it.findViewById(R.id.tvSubtitle)
            tvSubtitle = it.findViewById(R.id.tvSubtitle)
            tvConfigure = it.findViewById(R.id.tvConfigure)
            btnStartVpn = it.findViewById(R.id.btnStart)
            ivOnion = it.findViewById(R.id.ivStatus)
            ivOnionShadow = it.findViewById(R.id.ivShadow)
            progressBar = it.findViewById(R.id.progressBar)
            lvConnectedActions = it.findViewById(R.id.lvConnected)

            if (Prefs.isPowerUserMode()) {
                btnStartVpn.text = getString(R.string.connect)
            }

            if (!isNetworkAvailable(requireContext())) {
                doLayoutNoInternet(requireContext())
            } else {

                when (lastStatus) {
                    OrbotConstants.STATUS_OFF -> doLayoutOff()
                    OrbotConstants.STATUS_STARTING -> doLayoutStarting(requireContext())
                    OrbotConstants.STATUS_ON -> doLayoutOn(requireContext())
                    OrbotConstants.STATUS_STOPPING -> {}
                    else -> {doLayoutOff()}
                }

            }


        }

        return view
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun stopTorAndVpn() {
        sendIntentToService(OrbotConstants.ACTION_STOP)
        sendIntentToService(OrbotConstants.ACTION_STOP_VPN)
    }

    fun sendNewnymSignal() {
        sendIntentToService(TorControlCommands.SIGNAL_NEWNYM)
        ivOnion.animate().alpha(0f).duration = 500
        Handler().postDelayed({ ivOnion.animate().alpha(1f).duration = 500 }, 600)
    }

    private fun openExitNodeDialog() {
        ExitNodeDialogFragment(this).show(requireActivity().supportFragmentManager, "ExitNodeDialogFragment")
    }

    private fun startTorAndVpnDelay(@Suppress("SameParameterValue") ms: Long) = Handler(Looper.getMainLooper()).postDelayed({startTorAndVpn()}, ms)


     fun startTorAndVpn() {
        val vpnIntent = VpnService.prepare(requireActivity())
        if (vpnIntent != null && (!Prefs.isPowerUserMode())) {
            startActivityForResult(vpnIntent, OrbotActivity.REQUEST_CODE_VPN)
        } else {
            // todo we need to add a power user mode for users to start the VPN without tor
            Prefs.putUseVpn(!Prefs.isPowerUserMode())
            sendIntentToService(OrbotConstants.ACTION_START)

            if (!Prefs.isPowerUserMode())
                sendIntentToService(OrbotConstants.ACTION_START_VPN)
        }
    }

    fun refreshMenuList (context: Context) {

        val listItems = arrayListOf(OrbotMenuAction(R.string.btn_change_exit, 0) {openExitNodeDialog()},
            OrbotMenuAction(R.string.btn_refresh, R.drawable.ic_refresh) {sendNewnymSignal()},
            OrbotMenuAction(R.string.btn_tor_off, R.drawable.ic_power) {stopTorAndVpn()})
        if (OrbotActivity.CAN_DO_APP_ROUTING && (!Prefs.isPowerUserMode())) listItems.add(0, OrbotMenuAction(R.string.btn_choose_apps, R.drawable.ic_choose_apps) {
            startActivityForResult(Intent(requireActivity(), AppManagerActivity::class.java), OrbotActivity.REQUEST_VPN_APP_SELECT)
        })
        lvConnectedActions.adapter = OrbotMenuActionAdapter(context, listItems)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OrbotActivity.REQUEST_CODE_VPN && resultCode == AppCompatActivity.RESULT_OK) {
            startTorAndVpn()
        } else if (requestCode == OrbotActivity.REQUEST_CODE_SETTINGS && resultCode == AppCompatActivity.RESULT_OK) {
            // todo respond to language change extra data here...
        } else if (requestCode == OrbotActivity.REQUEST_VPN_APP_SELECT && resultCode == AppCompatActivity.RESULT_OK) {
            sendIntentToService(OrbotConstants.ACTION_RESTART_VPN) // is this enough todo?
            refreshMenuList(requireContext())
        }
    }

    private fun doLayoutForCircumventionApi() {
        Log.d("bim", "doLayoutForCircumventionApi")
        // TODO prompt user to request bridge over MOAT
        progressBar.progress = 0
        tvTitle.text = getString(R.string.having_trouble)
        tvSubtitle.text = getString(R.string.having_trouble_subtitle)
        tvSubtitle.visibility = View.VISIBLE
        btnStartVpn.text = getString(R.string.solve_captcha)
        btnStartVpn.setOnClickListener {
            MoatBottomSheet(this).show(requireActivity().supportFragmentManager, "CircumventionFailed")
        }
        tvConfigure.text = getString(android.R.string.cancel)
        tvConfigure.setOnClickListener {
            doLayoutOff()
        }
    }


    fun doLayoutNoInternet(context : Context) {

        ivOnion.setImageResource(R.drawable.nointernet)

        ivOnion.animation?.cancel()

        tvSubtitle.visibility = View.VISIBLE

        progressBar.visibility = View.INVISIBLE
        tvTitle.text = getString(R.string.no_interent_title)
        tvSubtitle.text = getString(R.string.no_internet_subtitle)

        tvSubtitle.visibility = View.VISIBLE

        btnStartVpn.visibility = View.GONE
        lvConnectedActions.visibility = View.GONE
        tvConfigure.visibility = View.GONE
        //refreshMenuList(context)

    }

    fun doLayoutOn(context : Context) {

        ivOnion.setImageResource(R.drawable.orbion)

        ivOnion.animation?.cancel()
        ivOnionShadow.animation?.cancel()
        ivOnion.animate().y(14f).duration = 200

        tvSubtitle.visibility = View.GONE
        progressBar.visibility = View.INVISIBLE
        tvTitle.text = context.getString(R.string.connected_title)
        tvSubtitle.visibility = View.GONE
        btnStartVpn.visibility = View.GONE
        lvConnectedActions.visibility = View.VISIBLE
        tvConfigure.visibility = View.GONE

        refreshMenuList(context)

        ivOnion.setOnClickListener {
            stopTorAndVpn()
        }
    }

    fun doLayoutOff() {

        ivOnion.setImageResource(R.drawable.orbioff)
        (ivOnion.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 200
        ivOnion.animation?.cancel()
        ivOnion.animate().y(200f).duration = 150

        tvSubtitle.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        lvConnectedActions.visibility = View.GONE
        tvTitle.text = getString(R.string.secure_your_connection_title)
        tvSubtitle.text = getString(R.string.secure_your_connection_subtitle)
        tvConfigure.visibility = View.VISIBLE
        tvConfigure.text = getString(R.string.btn_configure)
        tvConfigure.setOnClickListener {openConfigureTorConnection()}
        //  torStatsGroup.visibility = View.GONE
        // tvPorts.text = getString(R.string.ports_not_set)
        with(btnStartVpn) {
            visibility = View.VISIBLE

            var connectStr = ""
            if (Prefs.getConnectionPathway().equals(Prefs.PATHWAY_DIRECT))
                connectStr = context.getString(R.string.action_use) + ' ' + getString(R.string.direct_connect);
            else if (Prefs.getConnectionPathway().equals(Prefs.PATHWAY_SNOWFLAKE))
                connectStr = context.getString(R.string.action_use) + ' ' + getString(R.string.snowflake);
            else if (Prefs.getConnectionPathway().equals(Prefs.PATHWAY_SNOWFLAKE_AMP))
                connectStr = context.getString(R.string.action_use) + ' ' + getString(R.string.snowflake_amp);
            else if (Prefs.getConnectionPathway().equals(Prefs.PATHWAY_CUSTOM))
                connectStr = context.getString(R.string.action_use) + ' ' + getString(R.string.custom_bridge);

            text = if (Prefs.isPowerUserMode())
                getString(R.string.connect)
            else
                Html.fromHtml("<big>${getString(R.string.btn_start_vpn)}</big><br/><small>${connectStr}</small>")


            isEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orbot_btn_enabled_purple
                    )
                )
            }
            setOnClickListener { startTorAndVpn() }
            //logBottomSheet.resetLog()
        }

        ivOnion.setOnClickListener {
            startTorAndVpn()
        }


    }


    public fun doLayoutStarting(context : Context) {

        // torStatsGroup.visibility = View.VISIBLE
        tvSubtitle.visibility = View.GONE
        with(progressBar) {
            progress = 0
            visibility = View.VISIBLE
        }
        ivOnion.setImageResource(R.drawable.orbistarting)
        val animHover = AnimationUtils.loadAnimation(context, R.anim.hover);
        animHover.repeatCount = Animation.INFINITE
        animHover.repeatMode = Animation.REVERSE
        ivOnion.animation = animHover
        animHover.start()
        val animShadow = AnimationUtils.loadAnimation(context, R.anim.shadow);
        animShadow.repeatCount = Animation.INFINITE
        animShadow.repeatMode = Animation.REVERSE
        ivOnionShadow.animation = animShadow
        animShadow.start()

        tvTitle.text = context.getString(R.string.trying_to_connect_title)
        with(btnStartVpn) {
            text = context.getString(android.R.string.cancel)
            isEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        R.color.orbot_btn_enabled_purple
                    )
                )
            }
            setOnClickListener { stopTorAndVpn() }
        }
    }


    private fun openConfigureTorConnection() =
//        startActivity(Intent(this, TestConnectionActivity::class.java)) // TODO debugging
        ConfigConnectionBottomSheet(this).show(requireActivity().supportFragmentManager, OrbotActivity::class.java.simpleName)



    override fun tryConnecting() {
        startTorAndVpn() // TODO for now just start tor and VPN, we need to decouple this down the line
    }

    override fun onExitNodeSelected(exitNode: String, countryDisplayName: String) {

        //tor format expects "{" for country code
        Prefs.setExitNodes("{$exitNode}");

        sendIntentToService(
            Intent(requireActivity(), OrbotService::class.java)
                .setAction(OrbotConstants.CMD_SET_EXIT)
                .putExtra("exit", exitNode)
        )

        refreshMenuList(requireContext())

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConnectFragment().apply {
                /**
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }**/
            }
    }

    private fun sendIntentToService(intent: Intent) = ContextCompat.startForegroundService(requireActivity(), intent)
    private fun sendIntentToService(action: String) = sendIntentToService(
        android.content.Intent(
            requireActivity(),
            org.torproject.android.service.OrbotService::class.java
        ).apply {
        this.action = action
    })



}