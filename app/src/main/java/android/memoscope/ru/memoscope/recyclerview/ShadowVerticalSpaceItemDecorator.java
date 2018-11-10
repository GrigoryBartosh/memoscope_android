package android.memoscope.ru.memoscope.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ShadowVerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {

    private Drawable divider;

    public ShadowVerticalSpaceItemDecorator(Context context, int resId) {

        // Use a custom divider specified by a drawable
        this.divider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        // 1. Get the parent (RecyclerView) padding
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        // 2. Iterate items of the RecyclerView
        for (int childIdx = 0; childIdx < parent.getChildCount(); childIdx++) {

            // 3. Get the item
            View item = parent.getChildAt(childIdx);

            // 4. Determine the item's top and bottom with the divider
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) item.getLayoutParams();
            int top = item.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            // 5. Set the divider's bounds
            this.divider.setBounds(left, top, right, bottom);

            // 6. Draw the divider
            this.divider.draw(c);
        }
    }
}
