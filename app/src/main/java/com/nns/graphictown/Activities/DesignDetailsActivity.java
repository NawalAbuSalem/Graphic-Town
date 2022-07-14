package com.nns.graphictown.Activities;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.nns.graphictown.Model.Product.ProductData;
import com.nns.graphictown.R;


public class DesignDetailsActivity extends AppCompatActivity {

    private ProductData product;
    private TextView productName,productAmount, productDescription, productMaterialType, productDimension, productNumberOfPictures, productCompany;
    private ImageView productImage;
    private SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_details);
        product = (ProductData) getIntent().getSerializableExtra("product");
        productName = findViewById(R.id.design_name);
        productDescription = findViewById(R.id.design_description);
        productMaterialType = findViewById(R.id.design_material_type);
        productDimension = findViewById(R.id.design_material_dimension);
        productNumberOfPictures = findViewById(R.id.design_number_of_picture);
        productAmount=findViewById(R.id.design_amount);
        productCompany = findViewById(R.id.design_company);
        productImage = findViewById(R.id.design_image);
        spinKitView = findViewById(R.id.spin_kit);
        addProductInformation();

    }

    private void addProductInformation() {
        productName.setText(product.getTitle());
        productDescription.setText(product.getDescription());
        productMaterialType.setText(product.getMaterialType());
        productDimension.setText(product.getSize());
        productNumberOfPictures.setText(product.getImagesCount());
        productAmount.setText(product.getAmount());
        productCompany.setText(Html.fromHtml("<u>" + product.getAgentName() + "</u>"));
        String imageUri=product.getImagesUrls().get(0);
        System.out.println(imageUri);
        Glide.with(this).load(imageUri).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                spinKitView.setVisibility(View.GONE);
                return false;
            }
        }).into(productImage);
    }
    public void back(View view) {
        finish();
    }
    public void chooseDesignImages(View view) {
        Intent intent=new Intent(DesignDetailsActivity.this, ChooseImageActivity.class);
        intent.putExtra("product",product);
        startActivity(intent);
    }
    public void showCompanyCategory(View view) {
        Intent intent = new Intent(DesignDetailsActivity.this, CompanyCategoryActivity.class);
        intent.putExtra("CompanyID", product.getAgentId());
        intent.putExtra("CompanyName", product.getAgentName());
        startActivity(intent);
    }
}