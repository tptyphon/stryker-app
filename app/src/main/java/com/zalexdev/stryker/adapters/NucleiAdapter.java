package com.zalexdev.stryker.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.zalexdev.stryker.R;
import com.zalexdev.stryker.objects.NucleiVuln;
import com.zalexdev.stryker.utils.Preferences;

import java.util.List;

public class NucleiAdapter extends ListAdapter<NucleiVuln, NucleiAdapter.LocalViewHolder> {

    private final Preferences preferences;
    private final LayoutInflater layoutInflater;

    public NucleiAdapter(Preferences preferences, Context context) {
        super(DIFF_CALLBACK);
        this.preferences = preferences;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_vuln, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        NucleiVuln nucleiVuln = getItem(position);
        holder.bind(nucleiVuln);
    }

    public void updateList(List<NucleiVuln> newList) {
        submitList(newList);
    }

    class LocalViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView desc;
        private final TextView severity;
        private final ImageView icon;
        private final ImageView link;
        private final MaterialCardView cardView;

        LocalViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            severity = itemView.findViewById(R.id.severity);
            desc = itemView.findViewById(R.id.desc);
            link = itemView.findViewById(R.id.link);
        }

        void bind(NucleiVuln nucleiVuln) {
            title.setText(nucleiVuln.getName());
            desc.setText(nucleiVuln.getDescription());
            severity.setText(nucleiVuln.getSeverity());
            link.setOnClickListener(v -> copyToClipboard(v.getContext(), nucleiVuln.getUrl()));
            setSeverityColor(nucleiVuln.getSeverity());
        }

        private void setSeverityColor(String severityLevel) {
            int colorRes;
            switch (severityLevel) {
                case "low":
                    colorRes = R.color.blue1;
                    break;
                case "medium":
                    colorRes = R.color.yellow;
                    break;
                case "high":
                    colorRes = R.color.orange;
                    break;
                case "info":
                    colorRes = R.color.green;
                    break;
                default:
                    colorRes = R.color.red5;
                    break;
            }
            icon.setColorFilter(ContextCompat.getColor(preferences.getContext(), colorRes), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        private void copyToClipboard(Context context, String text) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Stryker", text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Snackbar.make(itemView, R.string.copied_to_clipboard, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(itemView, R.string.error_copying_to_clipboard, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private static final DiffUtil.ItemCallback<NucleiVuln> DIFF_CALLBACK = new DiffUtil.ItemCallback<NucleiVuln>() {
        @Override
        public boolean areItemsTheSame(@NonNull NucleiVuln oldItem, @NonNull NucleiVuln newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NucleiVuln oldItem, @NonNull NucleiVuln newItem) {
            return oldItem.equals(newItem);
        }
    };
}
