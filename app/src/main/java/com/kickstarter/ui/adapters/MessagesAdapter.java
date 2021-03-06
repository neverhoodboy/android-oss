package com.kickstarter.ui.adapters;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

import com.kickstarter.R;
import com.kickstarter.models.Message;
import com.kickstarter.ui.viewholders.KSViewHolder;
import com.kickstarter.ui.viewholders.MessageCenterTimestampViewHolder;
import com.kickstarter.ui.viewholders.MessageViewHolder;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public final class MessagesAdapter extends KSAdapter {
  public MessagesAdapter() {}

  private int getLayoutId(final @NonNull SectionRow sectionRow) {
    if (objectFromSectionRow(sectionRow) instanceof DateTime) {
      return R.layout.message_center_timestamp_layout;
    } else if (objectFromSectionRow(sectionRow) instanceof Message) {
      return R.layout.message_view;
    }
    return R.layout.empty_view;
  }

  public void messages(final @NonNull List<Message> messages) {
    // Group messages by start of day.
    Observable.from(messages)
      .groupBy(message -> message.createdAt().withTimeAtStartOfDay())
      .forEach(dateAndGroupedMessages -> {
        addSection(Collections.singletonList(dateAndGroupedMessages.getKey()));
        dateAndGroupedMessages
          .forEach(message -> addSection(Collections.singletonList(message)));
      });

    notifyDataSetChanged();
  }

  public void appendNewMessage(final @NonNull Pair<Message, Integer> messageAndPosition) {
    // Add a date view holder and a message body view holder.
    addSection(Collections.singletonList(messageAndPosition.first.createdAt()));
    addSection(Collections.singletonList(messageAndPosition.first));

    notifyItemInserted(messageAndPosition.second);
  }

  @Override
  protected int layout(final @NonNull SectionRow sectionRow) {
    return getLayoutId(sectionRow);
  }

  @Override
  public void onBindViewHolder(final @NonNull KSViewHolder holder, final int position,
    final @NonNull List<Object> payloads) {
    super.onBindViewHolder(holder, position, payloads);

    if (holder instanceof MessageViewHolder) {
      // Let the MessageViewHolder know if it is the last position in the RecyclerView.
      ((MessageViewHolder) holder).isLastPosition(position == getItemCount() - 1);
    }
  }

  @Override
  protected @NonNull KSViewHolder viewHolder(final @LayoutRes int layout, final @NonNull View view) {
    switch (layout) {
      case R.layout.message_center_timestamp_layout:
        return new MessageCenterTimestampViewHolder(view);
      case R.layout.message_view:
        return new MessageViewHolder(view);
      default:
        throw new IllegalStateException("Invalid layout.");
    }
  }
}
