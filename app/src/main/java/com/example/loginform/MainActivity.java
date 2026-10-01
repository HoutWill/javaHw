package com.example.loginform;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginform.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Inflate View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Underline Forgot Password link
        binding.tvForgotPassword.setPaintFlags(binding.tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        );

        // Setup Sign Up Span Link navigating to RegisterActivity
        String signUpText = getString(R.string.signup_prompt);
        SpannableString spannableString = new SpannableString(signUpText);
        int startIndex = signUpText.indexOf("Sign up");
        if (startIndex != -1) {
            int endIndex = startIndex + "Sign up".length();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                    ds.setColor(getColor(R.color.sky_blue));
                }
            };
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvSignUp.setText(spannableString);
            binding.tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
            binding.tvSignUp.setHighlightColor(Color.TRANSPARENT);
        }

        // Add TextWatchers to clear errors and update button state
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.usernameLayout.setError(null);
                binding.passwordLayout.setError(null);
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.usernameEditText.addTextChangedListener(textWatcher);
        binding.passwordEditText.addTextChangedListener(textWatcher);

        // Initialize button state
        updateLoginButtonState();

        // Focus listeners for soft keyboard
        binding.usernameEditText.setOnClickListener(this::showKeyboard);
        binding.passwordEditText.setOnClickListener(this::showKeyboard);

        binding.usernameEditText.post(() -> showKeyboard(binding.usernameEditText));

        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> performLogin());

        // Trigger login when user presses Enter/Done on keyboard
        binding.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLogin();
                return true;
            }
            return false;
        });
    }

    private void updateLoginButtonState() {
        String username = binding.usernameEditText.getText() != null ? binding.usernameEditText.getText().toString().trim() : "";
        String password = binding.passwordEditText.getText() != null ? binding.passwordEditText.getText().toString().trim() : "";
        boolean isFormValid = !username.isEmpty() && !password.isEmpty();

        binding.btnLogin.setEnabled(isFormValid);
        binding.btnLogin.setBackgroundTintList(ColorStateList.valueOf(
                isFormValid ? getColor(R.color.brand_teal) : Color.GRAY
        ));
    }

    private void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        }
    }

    private void performLogin() {
        hideKeyboard();

        String username = binding.usernameEditText.getText() != null ? binding.usernameEditText.getText().toString().trim() : "";
        String password = binding.passwordEditText.getText() != null ? binding.passwordEditText.getText().toString().trim() : "";

        binding.usernameLayout.setError(null);
        binding.passwordLayout.setError(null);

        boolean isValid = true;

        if (username.isEmpty()) {
            binding.usernameLayout.setError(getString(R.string.error_username_required));
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.passwordLayout.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            binding.passwordLayout.setError(getString(R.string.error_password_length));
            isValid = false;
        }

        if (!isValid) return;

        if (username.equals("admin") && password.equals("123456")) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Home/Dashboard Activity
        } else {
            binding.usernameLayout.setError(getString(R.string.error_invalid_credentials));
            binding.passwordLayout.setError(getString(R.string.error_invalid_credentials));
            Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show();
        }
    }
}
