package com.example.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButton btnNext;
    private LinearLayout dotsContainer;

    private final int[] icons = {
            R.drawable.ic_inventory,
            R.drawable.ic_qr_scan,
            R.drawable.ic_reports
    };
    private final String[] titles = {
            "Track Everything",
            "Scan & Add Fast",
            "Smart Reports"
    };
    private final String[] descs = {
            "Manage your entire inventory in one place. Add items, set prices, and monitor stock levels effortlessly.",
            "Use your camera to scan barcodes and instantly add or find items. No manual typing needed.",
            "Get real-time insights on your stock value, low stock alerts, and category breakdowns."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btn_next);
        dotsContainer = findViewById(R.id.dots_container);

        viewPager.setAdapter(new SlideAdapter());
        setupDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupDots(position);
                btnNext.setText(position == titles.length - 1 ? "Get Started" : "Next");
            }
        });

        btnNext.setOnClickListener(v -> {
            int next = viewPager.getCurrentItem() + 1;
            if (next < titles.length) {
                viewPager.setCurrentItem(next);
            } else {
                finish();
            }
        });

        findViewById(R.id.btn_skip).setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit().putBoolean("onboarding_done", true).apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.finish();
    }

    private void setupDots(int active) {
        dotsContainer.removeAllViews();
        for (int i = 0; i < titles.length; i++) {
            View dot = new View(this);
            int size = i == active ? 24 : 16;
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    dpToPx(size), dpToPx(8));
            p.setMarginEnd(dpToPx(6));
            dot.setLayoutParams(p);
            dot.setBackgroundResource(i == active ?
                    R.drawable.bg_icon_circle_primary : R.drawable.bg_badge_primary);
            dotsContainer.addView(dot);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideVH> {
        @NonNull
        @Override
        public SlideVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_onboarding_slide, parent, false);
            v.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return new SlideVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SlideVH h, int pos) {
            ((ImageView) h.itemView.findViewById(R.id.slide_icon)).setImageResource(icons[pos]);
            ((TextView) h.itemView.findViewById(R.id.slide_title)).setText(titles[pos]);
            ((TextView) h.itemView.findViewById(R.id.slide_desc)).setText(descs[pos]);
        }

        @Override
        public int getItemCount() { return titles.length; }

        class SlideVH extends RecyclerView.ViewHolder {
            SlideVH(@NonNull View v) { super(v); }
        }
    }
}
