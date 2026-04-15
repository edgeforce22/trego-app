package com.example.tregoapp.customer.ui;

import static com.example.tregoapp.BuildConfig.BASE_URL_ENDPOINT;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.network.SocketManager;
import com.example.tregoapp.customer.utils.EmptyStateHelper;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;

public class SOSSendingFragment extends Fragment {

    private static final String CUSTOMER_ID = "customer_id";
    private String customerId;
    private Socket socket;

    private Handler handler = new Handler(Looper.getMainLooper());

    public SOSSendingFragment() {
        // Required empty public constructor
    }

    public static SOSSendingFragment newInstance(String customerId) {
        SOSSendingFragment fragment = new SOSSendingFragment();
        Bundle args = new Bundle();
        args.putString(CUSTOMER_ID, customerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getString(CUSTOMER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_s_o_s_sending, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        View sosButton = view.findViewById(R.id.sosButton); // give id in XML
        animateSOSButton(sosButton);
        View pulseOuter = view.findViewById(R.id.pulseOuter);
        View pulseInner = view.findViewById(R.id.pulseInner);
        TextView tvStatus = view.findViewById(R.id.tvStatus);


        handler.postDelayed(() -> {
            if (!isAdded()) return;

            tvStatus.setText("Finding nearby mechanics...");
        }, 1500);

        handler.postDelayed(() -> {
            if (!isAdded()) return;

            tvStatus.setText("Connecting to mechanic...");
        }, 3000);

        startPulseAnimation(pulseOuter, 0, 3f);
        startPulseAnimation(pulseInner, 600, 2.5f);

        view.findViewById(R.id.btnCancelSOS).setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            // Toast.makeText(requireContext(), \"SOS Cancelled\", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), new DashboardFragment());
            }
        });

        // Initialize Socket
        initSocket();
    }

//    private void initSocket() {
//        try {
//            socket = IO.socket(com.example.tregoapp.BuildConfig.BASE_URL_ENDPOINT);
//            socket.connect();
//
//            socket.on(Socket.EVENT_CONNECT, args -> {
//                if (customerId != null) {
//                    socket.emit("join_customer", customerId);
//                }
//            });
//
//            socket.on("sos_accepted", args -> {
//                if (getActivity() == null) return;
//
//                JSONObject data = (JSONObject) args[0];
//                String requestId = data.optString("requestId");
//
//                getActivity().runOnUiThread(() -> {
//                    Toast.makeText(requireContext(), "Mechanic Found!", Toast.LENGTH_SHORT).show();
//                    NavigationHelper.navigateTo(getParentFragmentManager(),
//                            CustomerSideTrackingFragment.newInstance(requestId), true);
//                });
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void initSocket() {
        try {
            SocketManager.init(BASE_URL_ENDPOINT);
            socket = SocketManager.getSocket();

            if (socket == null) {
                Log.e("SOCKET_DEBUG", "Socket is null");
                return;
            }

            // ================= LISTEN FIRST =================
            socket.off("sos_accepted");
            socket.on("sos_accepted", args -> {

                Log.d("SOCKET_DEBUG", "🚨 SOS ACCEPTED RECEIVED");

                if (getActivity() == null || args.length == 0) return;

                try {
                    JSONObject data = (JSONObject) args[0];
                    String requestId = data.optString("requestId");

                    requireActivity().runOnUiThread(() -> {
                        // Toast.makeText(requireContext(), "Mechanic Found!", Toast.LENGTH_SHORT).show();

                        NavigationHelper.navigateTo(
                                getParentFragmentManager(),
                                CustomerSideTrackingFragment.newInstance(requestId),
                                true
                        );
                    });

                } catch (Exception e) {
                    Log.e("SOCKET_ERROR", "Parsing error", e);
                }
            });

            // ================= CONNECT =================
            if (!socket.connected()) {
                socket.connect();
                Log.d("SOCKET_DEBUG", "Connecting socket...");
            }

            // 🔥 FIX 1: JOIN IMMEDIATELY (VERY IMPORTANT)
            if (customerId != null) {
                socket.emit("join_customer", customerId);
                Log.d("SOCKET_DEBUG", "⚡ Immediate join: " + customerId);
            }

            // 🔥 FIX 2: ALSO JOIN ON CONNECT (FOR SAFETY)
            socket.off(Socket.EVENT_CONNECT);
            socket.on(Socket.EVENT_CONNECT, args -> {

                Log.d("SOCKET_DEBUG", "✅ Connected");

                if (customerId != null) {
                    socket.emit("join_customer", customerId);
                    Log.d("SOCKET_DEBUG", "👤 Joined after connect: " + customerId);
                }
            });

        } catch (Exception e) {
            Log.e("SOCKET_ERROR", "Socket init failed", e);
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        handler.removeCallbacksAndMessages(null);
//        if (socket != null) {
//            socket.off("sos_accepted");
//            socket.disconnect();
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (socket != null) {
            socket.off("sos_accepted");
            socket.off(Socket.EVENT_CONNECT);
        }

        handler.removeCallbacksAndMessages(null);
    }

    private void startPulseAnimation(View view, long delay, float scaleTo) {

        view.setScaleX(0.6f);
        view.setScaleY(0.6f);
        view.setAlpha(0.6f);

        view.animate()
                .scaleX(scaleTo)
                .scaleY(scaleTo)
                .alpha(0f)
                .setDuration(2000)
                .setStartDelay(delay)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .withEndAction(() -> startPulseAnimation(view, delay, scaleTo))
                .start();
    }


    private void animateSOSButton(View sosButton) {
        sosButton.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(800)
                .withEndAction(() -> sosButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(800)
                        .withEndAction(() -> animateSOSButton(sosButton))
                        .start())
                .start();
    }
}