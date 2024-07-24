package com.zalexdev.stryker.adapters;

import static com.zalexdev.stryker.utils.TextStyler.convert;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.zalexdev.stryker.R;
import com.zalexdev.stryker.objects.Device;
import com.zalexdev.stryker.objects.Port;
import com.zalexdev.stryker.utils.Preferences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalPortsAdapter extends RecyclerView.Adapter<LocalPortsAdapter.LocalViewHolder> {

    private List<Port> portItemList;

    private final Preferences preferences;


    public LocalPortsAdapter(List<Port> ports) {
        ports.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getPortNumber())));
        this.portItemList = ports;
        preferences = Preferences.getInstance();
        
    }

    

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_port, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Port port = portItemList.get(position);
        holder.port.setText(port.getPortNumber() + " (" + port.getPortName() + ")");
        holder.banner.setText(port.getBanner());
        if (port.getBanner().isEmpty()){
            holder.banner.setVisibility(View.GONE);
        }
        if (port.getPortNum() == 80 || port.getPortNum() == 8080) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.web)));
        } else if (port.getPortNum() == 443) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.web)));
        } else if (port.getPortNum() == 21) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.file_category)));
            holder.connect.setVisibility(View.VISIBLE);
            holder.brute.setVisibility(View.VISIBLE);
        } else if (port.getPortNum() == 22) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.terminal)));
            holder.connect.setVisibility(View.VISIBLE);
            holder.brute.setVisibility(View.VISIBLE);
        } else if (port.getPortNum() == 23) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.terminal)));
            holder.connect.setVisibility(View.VISIBLE);
            holder.brute.setVisibility(View.VISIBLE);
        } else if (port.getPortNum() == 25) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.mail)));
        } else if (port.getPortNum() == 53) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.website)));
        } else if (port.getPortNum() == 110) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.mail)));
        } else if (port.getPortNum() == 143) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.email)));
        } else if (port.getPortNum() == 389) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.captive)));
        } else if (port.getPortNum() == 443) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.web)));
        } else if (port.getPortNum() == 445) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.smb)));
            holder.connect.setVisibility(View.VISIBLE);
            holder.brute.setVisibility(View.VISIBLE);
        } else if (port.getPortNum() == 3306) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.db)));
            holder.connect.setVisibility(View.VISIBLE);
            holder.brute.setVisibility(View.VISIBLE);
        } else if (port.getPortNum() == 3389) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.windows)));
            holder.connect.setVisibility(View.VISIBLE);
            holder.brute.setVisibility(View.VISIBLE);
        } else if (port.getPortNum() == 5432) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.db)));
        } else if (port.getPortNum() == 5900) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.windows)));
            holder.connect.setVisibility(View.VISIBLE);
            holder.brute.setVisibility(View.VISIBLE);
        } else if (port.getPortNum() == 8080) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.web)));
        } else if (port.getPortNum() == 8443) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.web)));
        } else if (port.getPortNum() == 9090) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.web)));
        } else if (port.getPortNum() == 9100) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.printer)));
        } else if (port.getPortNum() == 9200) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.db)));
        } else if (port.getPortNum() == 9300) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.db)));
        } else if (port.getPortNum() > 27017 && port.getPortNum() < 28017) {
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.db)));
        }else{
            holder.img.setImageDrawable(Objects.requireNonNull(holder.itemView.getContext().getDrawable(R.drawable.question)));
        }




    }

    @Override
    public int getItemCount() {
        return portItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class LocalViewHolder extends RecyclerView.ViewHolder {
        private final TextView port;
        private final TextView banner;
        private final ImageView img;
        private final MaterialButton connect;
        private final MaterialButton brute;
        private final MaterialCardView cardView;


        LocalViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);
            img = itemView.findViewById(R.id.image);
            port = itemView.findViewById(R.id.port);
            banner = itemView.findViewById(R.id.banner);
            connect = itemView.findViewById(R.id.connect);
            brute = itemView.findViewById(R.id.bruteforce);

        }





         void copyToClipboard(Context context, String text) {

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("Stryker", text);

            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Snackbar.make(itemView, R.string.copied_to_clipboard, Snackbar.LENGTH_SHORT).show();
            }else {
                Snackbar.make(itemView, R.string.error_copying_to_clipboard, Snackbar.LENGTH_SHORT).show();
            }
        }





        public void appendToTerminal(String text, TextView terminal) {
            if (preferences.isHideMac()){
                Matcher m = Pattern.compile("((\\w{2}:){5}\\w{2})").matcher(text);
                while (m.find()) {
                    text = text.replace(m.group(), "XX:XX:XX:XX:XX:XX");
                }
            }
            terminal.append(convert(text+"<br>"));
        }

        public void spaceToTerminal(TextView terminal) {
            terminal.append("\n");
        }

        public boolean containsPin(String pin){
            Pattern pattern = Pattern.compile("\\b\\d{8}\\b");
            Matcher matcher = pattern.matcher(pin);
            return matcher.find();
        }

        public String getPin(String text){
            Pattern pattern = Pattern.compile("\\b\\d{8}\\b");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group();
            }else {
                return null;
            }
        }

        public ArrayList<String> getPins(ArrayList<String> list){
            ArrayList<String> pins = new ArrayList<>();
            for (String s : list) {
                if (containsPin(s)) {
                    pins.add(getPin(s));
                }
            }
            return pins;
        }

    }




}
