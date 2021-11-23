package com.jachdev.consumerprotection.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.commonlibs.validator.Validator;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.ui.SignUpActivity;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.et_password)
    CustomEditText etPassword;
    @BindView(R.id.et_phone)
    CustomEditText etPhone;
    @BindView(R.id.et_pin)
    CustomEditText etPin;

    private AppService service;
    private FirebaseAuth mAuth;

    @Override
    protected int layoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = AppApplication.getInstance().getAppService();
        mAuth = FirebaseAuth.getInstance();

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null && deepLink.getQueryParameter("continueUrl").contains(etEmail.getTrimText())){
                            Uri uri = Uri.parse(deepLink.getQueryParameter("continueUrl"));
                            String email = uri.getQueryParameter("email");
                            User user = SessionManager.getInstance().getUser();
                            if(user.getEmail().equalsIgnoreCase(email)){
                                verifyPhone(user.getNumber());
                            }else{
                                showError("Email authentication has failed.");
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    public void onSignUpClick(View view) {
        activityToActivity(SignUpActivity.class);
    }

    public void onSignInClick(View view) {
        if(!Validator.isValidEmail(etEmail) || !Validator.isValidPassword(etPassword) || !Validator.isValidField(etPhone)){
            return;
        }

        callSignIn();
    }

    public void onSubmitClick(View view) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, etPin.getTextString());

        signInWithPhoneAuthCredential(credential);
    }

    public void onCancelClick(View view) {
        findViewById(R.id.layout_pin).setVisibility(View.GONE);
        findViewById(R.id.layout_sign_in).setVisibility(View.VISIBLE);
    }

    private void callSignIn() {
        User user = new User();
        user.setEmail(etEmail.getTrimText());
        user.setPassword(etPassword.getTrimText());

        service.getServer().login(user)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<AppResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NotNull AppResponse response) {
                        switch (response.getCode()){
                            case 0:

                                mUser = response.getObjectToType(User.class);
                                mUser.setNumber(etPhone.getTextString());
                                SessionManager.getInstance().setUser(mUser);

                                signInWithEmailAuthCredential(etEmail.getTrimText());

                                break;
                            default:
                                showError(response.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private void verifyPhone(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showError(String message){

        if(message == null)
            message = "Unknown error.";

        ChocoBar.builder().setBackgroundColor(Color.parseColor("#FFA61B1B"))
                .setTextSize(18)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(message)
                .setMaxLines(4)
                .centerText()
                .setActionText(getString(R.string.ok))
                .setActivity(this)
                .build()
                .show();
    }

    private String mVerificationId = null;
    private PhoneAuthProvider.ForceResendingToken mResendToken = null;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted:" + credential);

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
            showError("Invalid phone number.");
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;


            findViewById(R.id.layout_sign_in).setVisibility(View.GONE);
            findViewById(R.id.layout_pin).setVisibility(View.VISIBLE);

        }
    };

    private void signInWithEmailAuthCredential(String email) {
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://consumerprotection.page.link/finishSignUp?email=" + email)
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.jachdev.consumerprotection.ios")
                        .setAndroidPackageName(
                                "com.jachdev.consumerprotection",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessage("Please verify your email by the click the link we sent to.");
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private User mUser;

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            SessionManager.getInstance().setLoggedIn(true);
                            activityToActivity(HomeActivity.class);
                            LoginActivity.this.finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showError(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }
}