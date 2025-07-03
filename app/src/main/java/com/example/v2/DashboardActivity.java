package com.example.v2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Main CoordinatorLayout
        CoordinatorLayout coordinatorLayout = createCoordinatorLayout();
        setContentView(coordinatorLayout);

        // Add AppBar
        coordinatorLayout.addView(createAppBarLayout());

        // Add NestedScrollView with all content
        coordinatorLayout.addView(createNestedScrollView());

        // Add BottomNavigation
        coordinatorLayout.addView(createBottomNavigation());
    }

    private CoordinatorLayout createCoordinatorLayout() {
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
        coordinatorLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
        ));
        coordinatorLayout.setBackgroundColor(getResources().getColor(R.color.background));
        return coordinatorLayout;
    }

    private AppBarLayout createAppBarLayout() {
        AppBarLayout appBarLayout = new AppBarLayout(this);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
        );
        appBarLayout.setLayoutParams(params);
        appBarLayout.setBackgroundColor(getResources().getColor(R.color.primary));

        Toolbar toolbar = new Toolbar(this);
        toolbar.setLayoutParams(new AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.MATCH_PARENT,
                (int) getTypedValue(android.R.attr.actionBarSize)
        ));
        toolbar.setTitle("Stock Manager");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        appBarLayout.addView(toolbar);
        return appBarLayout;
    }

    private NestedScrollView createNestedScrollView() {
        NestedScrollView scrollView = new NestedScrollView(this);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
        );
        params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        scrollView.setLayoutParams(params);

        LinearLayout mainContent = createMainContentLayout();
        scrollView.addView(mainContent);

        return scrollView;
    }

    private LinearLayout createMainContentLayout() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(16);
        mainLayout.setPadding(padding, padding, padding, padding);

        // Add all content views
        mainLayout.addView(createWelcomeSection());
        mainLayout.addView(createSectionTitle("Quick Actions"));
        mainLayout.addView(createQuickActionsRow1());
        mainLayout.addView(createQuickActionsRow2());
        mainLayout.addView(createSectionTitle("Inventory Stats"));
        mainLayout.addView(createTotalItemsCard());
        mainLayout.addView(createStatsRow());
        mainLayout.addView(createSectionTitle("Recent Activity"));
        mainLayout.addView(createRecentActivityRecycler());
        mainLayout.addView(createEmptyActivitiesText());

        return mainLayout;
    }

    private LinearLayout createWelcomeSection() {
        LinearLayout welcomeLayout = new LinearLayout(this);
        welcomeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        welcomeLayout.setOrientation(LinearLayout.VERTICAL);

        TextView welcomeText = new MaterialTextView(this);
        welcomeText.setId(R.id.welcomeText);
        welcomeText.setText("Welcome, Varad");
        welcomeText.setTextAppearance(this, R.style.MyTitleStyle);
        welcomeText.setTextColor(getResources().getColor(R.color.text_primary));
        welcomeText.setTypeface(null, Typeface.BOLD);

        TextView subtitle = new MaterialTextView(this);
        subtitle.setText("Manage your inventory efficiently");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dpToPx(4);
        subtitle.setLayoutParams(params);
        subtitle.setTextAppearance(this, R.style.MyBodyStyle);
        subtitle.setTextColor(getResources().getColor(R.color.text_secondary));

        welcomeLayout.addView(welcomeText);
        welcomeLayout.addView(subtitle);

        return welcomeLayout;
    }

    private TextView createSectionTitle(String title) {
        TextView titleView = new MaterialTextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dpToPx(24);
        titleView.setLayoutParams(params);
        titleView.setText(title);
        titleView.setTextAppearance(this, R.style.MyHeadline6);
        titleView.setTextColor(getResources().getColor(R.color.text_primary));
        titleView.setTypeface(null, Typeface.BOLD);
        return titleView;
    }

    private LinearLayout createQuickActionsRow1() {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dpToPx(8), 0, 0);

        // Add Item Card
        MaterialCardView addItemCard = createActionCard(
                "+", "Add Item", R.color.primary_light, R.id.addItemCard
        );
        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(
                0,
                dpToPx(120),
                1
        );
        addParams.setMarginEnd(dpToPx(8));
        addItemCard.setLayoutParams(addParams);

        // View Inventory Card
        MaterialCardView viewInventoryCard = createActionCardWithIcon(
                R.drawable.ic_inventory, "View Inventory", R.color.secondary, R.id.viewInventoryCard
        );
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
                0,
                dpToPx(120),
                1
        );
        viewParams.setMarginStart(dpToPx(8));
        viewInventoryCard.setLayoutParams(viewParams);

        row.addView(addItemCard);
        row.addView(viewInventoryCard);
        return row;
    }

    private LinearLayout createQuickActionsRow2() {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dpToPx(12), 0, 0);

        // Scan Item Card
        MaterialCardView scanItemCard = createActionCardWithIcon(
                R.drawable.ic_qr_scan, "Scan Item", R.color.warning, R.id.scanItemCard
        );
        LinearLayout.LayoutParams scanParams = new LinearLayout.LayoutParams(
                0,
                dpToPx(120),
                1
        );
        scanParams.setMarginEnd(dpToPx(8));
        scanItemCard.setLayoutParams(scanParams);

        // Delete Item Card
        MaterialCardView deleteItemCard = createActionCardWithIcon(
                R.drawable.ic_delete, "Delete Item", R.color.error, R.id.deleteItemCard
        );
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                0,
                dpToPx(120),
                1
        );
        deleteParams.setMarginStart(dpToPx(8));
        deleteItemCard.setLayoutParams(deleteParams);

        row.addView(scanItemCard);
        row.addView(deleteItemCard);
        return row;
    }

    private MaterialCardView createActionCard(String iconText, String title, int bgColorRes, int viewId) {
        MaterialCardView card = new MaterialCardView(this);
        card.setId(viewId);
        card.setClickable(true);
        card.setFocusable(true);
        card.setCardBackgroundColor(getResources().getColor(R.color.white));
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(2));

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setGravity(Gravity.CENTER);
        cardContent.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        // Icon container
        MaterialCardView iconContainer = new MaterialCardView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(48),
                dpToPx(48)
        );
        iconContainer.setLayoutParams(iconParams);
        iconContainer.setCardBackgroundColor(getResources().getColor(bgColorRes));
        iconContainer.setRadius(dpToPx(24));
        iconContainer.setCardElevation(0);

        // Icon text
        TextView iconTextVew = new TextView(this);
        iconTextVew.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        iconTextVew.setGravity(Gravity.CENTER);
        iconTextVew.setText(iconText);
        iconTextVew.setTextColor(Color.WHITE);
        iconTextVew.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        iconTextVew.setTypeface(null, Typeface.BOLD);

        iconContainer.addView(iconTextVew);

        // Title
        TextView titleView = new MaterialTextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = dpToPx(8);
        titleView.setLayoutParams(titleParams);
        titleView.setText(title);
        titleView.setTextAppearance(this, R.style.MyBodyStyle);
        titleView.setTextColor(getResources().getColor(R.color.text_primary));

        cardContent.addView(iconContainer);
        cardContent.addView(titleView);
        card.addView(cardContent);

        // Add click listener for card
        card.setOnClickListener(v -> {
            if (viewId == R.id.addItemCard) {
                openAddItemActivity();
            }
        });

        return card;
    }

    private MaterialCardView createActionCardWithIcon(int iconRes, String title, int bgColorRes, int viewId) {
        MaterialCardView card = new MaterialCardView(this);
        card.setId(viewId);
        card.setClickable(true);
        card.setFocusable(true);
        card.setCardBackgroundColor(getResources().getColor(R.color.white));
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(2));

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setGravity(Gravity.CENTER);
        cardContent.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        // Icon container
        MaterialCardView iconContainer = new MaterialCardView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(48),
                dpToPx(48)
        );
        iconContainer.setLayoutParams(iconParams);
        iconContainer.setCardBackgroundColor(getResources().getColor(bgColorRes));
        iconContainer.setRadius(dpToPx(24));
        iconContainer.setCardElevation(0);

        // Icon
        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(24),
                dpToPx(24)
        ));
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setImageResource(iconRes);
        icon.setColorFilter(Color.WHITE);

        iconContainer.addView(icon);

        // Title
        TextView titleView = new MaterialTextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = dpToPx(8);
        titleView.setLayoutParams(titleParams);
        titleView.setText(title);
        titleView.setTextAppearance(this, R.style.MyBodyStyle);
        titleView.setTextColor(getResources().getColor(R.color.text_primary));

        cardContent.addView(iconContainer);
        cardContent.addView(titleView);
        card.addView(cardContent);

        // Handle clicks for all icon-based cards
        card.setOnClickListener(v -> {
            if (viewId == R.id.viewInventoryCard) {
                openViewInventoryActivity();
            } else if (viewId == R.id.scanItemCard) {
                openScanItemActivity();
            } else if (viewId == R.id.deleteItemCard) {
                openDeleteItemActivity();
            }
        });

        return card;
    }

    private MaterialCardView createTotalItemsCard() {
        MaterialCardView card = new MaterialCardView(this);
        card.setId(R.id.totalItemsCard);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dpToPx(8);
        card.setLayoutParams(params);
        card.setCardBackgroundColor(getResources().getColor(R.color.white));
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(2));

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Icon container
        MaterialCardView iconContainer = new MaterialCardView(this);
        iconContainer.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(48),
                dpToPx(48)
        ));
        iconContainer.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
        iconContainer.setRadius(dpToPx(24));
        iconContainer.setCardElevation(0);

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(24),
                dpToPx(24)
        ));
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setImageResource(R.drawable.ic_item_count);
        icon.setColorFilter(Color.WHITE);

        iconContainer.addView(icon);

        // Text content
        LinearLayout textContent = new LinearLayout(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMarginStart(dpToPx(16));
        textContent.setLayoutParams(textParams);
        textContent.setOrientation(LinearLayout.VERTICAL);

        TextView label = new MaterialTextView(this);
        label.setText("ITEMS IN STOCK");
        label.setTextAppearance(this, R.style.MyCaption);
        label.setTextColor(getResources().getColor(R.color.text_secondary));

        TextView value = new MaterialTextView(this);
        value.setId(R.id.totalItemsText);
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        valueParams.topMargin = dpToPx(4);
        value.setLayoutParams(valueParams);
        value.setText("0");
        value.setTextAppearance(this, R.style.MyHeadline5);
        value.setTextColor(getResources().getColor(R.color.text_primary));
        value.setTypeface(null, Typeface.BOLD);

        textContent.addView(label);
        textContent.addView(value);

        cardContent.addView(iconContainer);
        cardContent.addView(textContent);
        card.addView(cardContent);

        return card;
    }

    private LinearLayout createStatsRow() {
        LinearLayout row = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dpToPx(12);
        row.setLayoutParams(params);
        row.setOrientation(LinearLayout.HORIZONTAL);

        // Low Stock Card
        MaterialCardView lowStockCard = createStatCard(
                R.drawable.ic_warning, "LOW STOCK", "0", R.color.error, R.id.lowStockText
        );
        LinearLayout.LayoutParams lowStockParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        lowStockParams.setMarginEnd(dpToPx(6));
        lowStockCard.setLayoutParams(lowStockParams);

        // Total Value Card
        MaterialCardView totalValueCard = createStatCardWithTextIcon(
                "₹", "TOTAL VALUE", "₹0", R.color.success, R.id.totalValueText
        );
        LinearLayout.LayoutParams totalValueParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        totalValueParams.setMarginStart(dpToPx(6));
        totalValueCard.setLayoutParams(totalValueParams);

        row.addView(lowStockCard);
        row.addView(totalValueCard);
        return row;
    }

    private MaterialCardView createStatCard(int iconRes, String label, String value, int bgColorRes, int valueViewId) {
        MaterialCardView card = new MaterialCardView(this);
        card.setCardBackgroundColor(getResources().getColor(R.color.white));
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(2));

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Icon container
        MaterialCardView iconContainer = new MaterialCardView(this);
        iconContainer.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        ));
        iconContainer.setCardBackgroundColor(getResources().getColor(bgColorRes));
        iconContainer.setRadius(dpToPx(20));
        iconContainer.setCardElevation(0);

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(20),
                dpToPx(20)
        ));
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setImageResource(iconRes);
        icon.setColorFilter(Color.WHITE);

        iconContainer.addView(icon);

        // Text content
        LinearLayout textContent = new LinearLayout(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMarginStart(dpToPx(12));
        textContent.setLayoutParams(textParams);
        textContent.setOrientation(LinearLayout.VERTICAL);

        TextView labelView = new MaterialTextView(this);
        labelView.setText(label);
        labelView.setTextAppearance(this, R.style.MyCaption);
        labelView.setTextColor(getResources().getColor(R.color.text_secondary));

        TextView valueView = new MaterialTextView(this);
        valueView.setId(valueViewId);
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        valueParams.topMargin = dpToPx(4);
        valueView.setLayoutParams(valueParams);
        valueView.setText(value);
        valueView.setTextAppearance(this, R.style.MyHeadline6);
        valueView.setTextColor(getResources().getColor(bgColorRes));
        valueView.setTypeface(null, Typeface.BOLD);

        textContent.addView(labelView);
        textContent.addView(valueView);

        cardContent.addView(iconContainer);
        cardContent.addView(textContent);
        card.addView(cardContent);

        return card;
    }

    private MaterialCardView createStatCardWithTextIcon(String iconText, String label, String value, int bgColorRes, int valueViewId) {
        MaterialCardView card = new MaterialCardView(this);
        card.setCardBackgroundColor(getResources().getColor(R.color.white));
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(2));

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Icon container
        MaterialCardView iconContainer = new MaterialCardView(this);
        iconContainer.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        ));
        iconContainer.setCardBackgroundColor(getResources().getColor(bgColorRes));
        iconContainer.setRadius(dpToPx(20));
        iconContainer.setCardElevation(0);

        TextView iconTextView = new TextView(this);
        iconTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        iconTextView.setGravity(Gravity.CENTER);
        iconTextView.setText(iconText);
        iconTextView.setTextColor(Color.WHITE);
        iconTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        iconTextView.setTypeface(null, Typeface.BOLD);

        iconContainer.addView(iconTextView);

        // Text content
        LinearLayout textContent = new LinearLayout(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMarginStart(dpToPx(12));
        textContent.setLayoutParams(textParams);
        textContent.setOrientation(LinearLayout.VERTICAL);

        TextView labelView = new MaterialTextView(this);
        labelView.setText(label);
        labelView.setTextAppearance(this, R.style.MyCaption);
        labelView.setTextColor(getResources().getColor(R.color.text_secondary));

        TextView valueView = new MaterialTextView(this);
        valueView.setId(valueViewId);
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        valueParams.topMargin = dpToPx(4);
        valueView.setLayoutParams(valueParams);
        valueView.setText(value);
        valueView.setTextAppearance(this, R.style.MyHeadline6);
        valueView.setTextColor(getResources().getColor(R.color.text_primary));
        valueView.setTypeface(null, Typeface.BOLD);

        textContent.addView(labelView);
        textContent.addView(valueView);

        cardContent.addView(iconContainer);
        cardContent.addView(textContent);
        card.addView(cardContent);

        return card;
    }

    private RecyclerView createRecentActivityRecycler() {
        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dpToPx(8);
        recyclerView.setLayoutParams(params);
        recyclerView.setId(R.id.recentActivityRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        // Set your adapter here
        return recyclerView;
    }

    private TextView createEmptyActivitiesText() {
        TextView emptyText = new MaterialTextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dpToPx(16);
        emptyText.setLayoutParams(params);
        emptyText.setId(R.id.emptyActivitiesText);
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setText("No recent activity");
        emptyText.setTextAppearance(this, R.style.MyBodyStyle);
        emptyText.setTextColor(getResources().getColor(R.color.text_secondary));
        emptyText.setVisibility(View.GONE);
        return emptyText;
    }

    private BottomNavigationView createBottomNavigation() {
        BottomNavigationView bottomNav = new BottomNavigationView(this);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM;
        bottomNav.setLayoutParams(params);
        bottomNav.setId(R.id.bottomNavigation);
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);
        return bottomNav;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private float getTypedValue(int attr) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attr, typedValue, true);
        return TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
    }

    // ====== Navigation Methods ====== //
    private void openAddItemActivity() {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openViewInventoryActivity() {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openScanItemActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openDeleteItemActivity() {
        Intent intent = new Intent(this, DeleteItemActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}