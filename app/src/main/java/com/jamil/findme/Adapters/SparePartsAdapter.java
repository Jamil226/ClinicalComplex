package com.jamil.findme.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jamil.findme.Activities.AddGeneralRepairPost;
import com.jamil.findme.Models.PostModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;

public class SparePartsAdapter extends RecyclerView.Adapter<SparePartsAdapter.ViewHolder> {
    private ArrayList<PostModel> arrayList;
    private String TAG = "TAG";
    private Context context;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private User currentUser;
    private PreferencesManager prefs;
    Bitmap bitmap;
    boolean boolean_permission,boolean_save;
    ProgressDialog progressDialog;
    public static int REQUEST_PERMISSIONS = 1;

    public SparePartsAdapter(ArrayList<PostModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public SparePartsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_spareparts, parent, false);
        context = parent.getContext();
        setupProgressDialog();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(context);
        prefs = new PreferencesManager(context);
        currentUser = prefs.getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SparePartsAdapter.ViewHolder holder, final int position) {
        final PostModel postModel = arrayList.get(position);
        Glide.with(context).load(postModel.getImage()).into(holder.ivspitem);
        holder.tvpNamespitem.setText(postModel.getProductName());
        holder.tvpricespitem.setText("$" + postModel.getPrice());
        holder.tvWorkShopspitem.setText(postModel.getWorkShop());
        holder.desc.setText(postModel.getDescription());
        holder.tvpTypespitem.setText(postModel.getType());
        holder.tvModelspitem.setText(postModel.getModel());
        holder.btnCreatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    progressDialog.show();
                    bitmap = loadBitmapFromView(holder.cvInvoice, holder.cvInvoice.getWidth(), holder.cvInvoice.getHeight());
                    createPdf(context, holder, postModel);
            }
        });

    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Creating Invoice");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
    }
    private void createPdf(Context context, ViewHolder holder, PostModel
            postModel) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        canvas.drawPaint(paint);


        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);


        String path = Environment.getExternalStoragePublicDirectory("Find Me").toString() ;
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        File filePath = new File(dir, postModel.getProductName() + ".pdf");

        try {
            document.writeTo(new FileOutputStream(filePath));
            progressDialog.dismiss();
            boolean_save=true;
            try {
                shareFile(filePath);
            }catch (Exception e){

                Log.e(TAG, "createPdf: EXP Share"+e.toString() );            }
            Toast.makeText(context, "Invoice Saved In the Storage", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.context, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }
    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://"+file.getAbsolutePath()));
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

       context.startActivity(Intent.createChooser(intentShareFile, "Share File"));

    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView desc, tvpNamespitem, tvpricespitem, tvWorkShopspitem, tvpTypespitem, tvModelspitem;
        ImageView ivspitem;
        CardView cvInvoice;
        Button btnCreatePdf;

        public ViewHolder(View view) {
            super(view);
            btnCreatePdf = view.findViewById(R.id.btnCreatePdf);
            cvInvoice = view.findViewById(R.id.cvInvoice);
            tvpTypespitem = view.findViewById(R.id.tvpTypespitem);
            tvModelspitem = view.findViewById(R.id.tvModelspitem);
            tvWorkShopspitem = view.findViewById(R.id.tvWorkShopspitem);
            tvpricespitem = view.findViewById(R.id.tvpricespitem);
            tvpNamespitem = view.findViewById(R.id.tvpNamespitem);
            ivspitem = view.findViewById(R.id.ivspitem);
            desc = view.findViewById(R.id.desc);
        }
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }

}
