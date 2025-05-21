package org.thoughtcrime.securesms.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.util.ThemeUtil;

public class AlertView extends AppCompatImageView {

  public AlertView(Context context) {
    this(context, null);
  }

  public AlertView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize();
  }

  public AlertView(final Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize();
  }

  private void initialize() {
    setImageResource(R.drawable.symbol_error_circle_compact_16);
    setScaleType(ScaleType.FIT_CENTER);
  }

  public void setNone() {
    setVisibility(View.GONE);
  }

  public void setFailed() {
    setVisibility(View.VISIBLE);
    setColorFilter(ThemeUtil.getThemedColor(getContext(), com.google.android.material.R.attr.colorError));
    setContentDescription(getContext().getString(R.string.conversation_item_sent__send_failed_indicator_description));
  }

  public void setRateLimited() {
    setVisibility(View.VISIBLE);
    setColorFilter(ThemeUtil.getThemedColor(getContext(), com.google.android.material.R.attr.colorOnSurfaceVariant));
    setContentDescription(getContext().getString(R.string.conversation_item_sent__pending_approval_description));
  }
}
