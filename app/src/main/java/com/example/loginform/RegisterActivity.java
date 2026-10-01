package com.example.loginform;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.example.loginform.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Inflate View Binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.registerMain, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Auto-scroll when focusing on Confirm Password field so input is fully visible
        binding.confirmPasswordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.registerMain.postDelayed(() ->
                        binding.registerMain.smoothScrollTo(0, binding.confirmPasswordLayout.getBottom()), 200);
            }
        });

        // Setup "Already have an account? Log in" Span Link
        String loginText = getString(R.string.login_prompt);
        SpannableString spannableString = new SpannableString(loginText);
        int startIndex = loginText.indexOf("Log in");
        if (startIndex != -1) {
            int endIndex = startIndex + "Log in".length();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                    ds.setColor(getColor(R.color.sky_blue));
                }
            };
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvLogin.setText(spannableString);
            binding.tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
            binding.tvLogin.setHighlightColor(Color.TRANSPARENT);
        }

        // Add TextWatcher to all 4 input fields to clear errors and update button state
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.emailLayout.setError(null);
                binding.usernameLayout.setError(null);
                binding.passwordLayout.setError(null);
                binding.confirmPasswordLayout.setError(null);
                updateRegisterButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.emailEditText.addTextChangedListener(textWatcher);
        binding.usernameEditText.addTextChangedListener(textWatcher);
        binding.passwordEditText.addTextChangedListener(textWatcher);
        binding.confirmPasswordEditText.addTextChangedListener(textWatcher);

        // Initialize button state
        updateRegisterButtonState();

        // Register button click listener
        binding.btnRegister.setOnClickListener(v -> performRegister());

        // Trigger registration on keyboard Done
        binding.confirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performRegister();
                return true;
            }
            return false;
        });
    }

    private void updateRegisterButtonState() {
        String email = binding.emailEditText.getText() != null ? binding.emailEditText.getText().toString().trim() : "";
        String username = binding.usernameEditText.getText() != null ? binding.usernameEditText.getText().toString().trim() : "";
        String password = binding.passwordEditText.getText() != null ? binding.passwordEditText.getText().toString().trim() : "";
        String confirmPassword = binding.confirmPasswordEditText.getText() != null ? binding.confirmPasswordEditText.getText().toString().trim() : "";

        boolean isFormFilled = !email.isEmpty() && !username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();

        binding.btnRegister.setEnabled(isFormFilled);
        binding.btnRegister.setBackgroundTintList(ColorStateList.valueOf(
                isFormFilled ? getColor(R.color.brand_teal) : Color.GRAY
        ));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        }
    }

    private void performRegister() {
        hideKeyboard();

        String email = binding.emailEditText.getText() != null ? binding.emailEditText.getText().toString().trim() : "";
        String username = binding.usernameEditText.getText() != null ? binding.usernameEditText.getText().toString().trim() : "";
        String password = binding.passwordEditText.getText() != null ? binding.passwordEditText.getText().toString().trim() : "";
        String confirmPassword = binding.confirmPasswordEditText.getText() != null ? binding.confirmPasswordEditText.getText().toString().trim() : "";

        // Reset errors
        binding.emailLayout.setError(null);
        binding.usernameLayout.setError(null);
        binding.passwordLayout.setError(null);
        binding.confirmPasswordLayout.setError(null);

        boolean isValid = true;

        if (email.isEmpty()) {
            binding.emailLayout.setError(getString(R.string.error_email_required));
            isValid = false;
        }

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

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.confirmPasswordLayout.setError(getString(R.string.error_confirm_password));
            isValid = false;
        }

        if (!isValid) return;

        Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
        // Navigate to Login (MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
