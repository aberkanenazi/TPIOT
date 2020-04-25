package com.esipe.tpiotesp32;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mData;
    private String macadress;

    public ListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<BluetoothDevice> data) {
        mData = data;
    }

    public int getCount() {
        return (mData == null) ? 0 : mData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public String getMacadress() {
        return macadress;
    }

    public void setMacadress(String macadress) {
        this.macadress = macadress;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView			=  mInflater.inflate(R.layout.list_device, null);

            holder 				= new ViewHolder();

            holder.deviceName		= (TextView) convertView.findViewById(R.id.devicename);
            holder.macAdress 	= (TextView) convertView.findViewById(R.id.mac_adresse);
            holder.pairBtn		= (Button) convertView.findViewById(R.id.btn_pair);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BluetoothDevice device	= mData.get(position);

        holder.deviceName.setText(device.getName());
        holder.macAdress.setText(device.getAddress());
        holder.pairBtn.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Unpair" : "Pair");
        holder.pairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                   unpairDevice(device);
                   holder.pairBtn.setText("Pair");
                   macadress="";
               }else{
                   setMacadress(device.getAddress());

                   pairDevice(device);

               }
            }
        });


        return convertView;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView macAdress;
        TextView pairBtn;
    }

    public interface OnPairButtonClickListener {
        public abstract void onPairButtonClick(int position);
    }
    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
