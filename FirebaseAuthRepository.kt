package com.moodloop.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.Locale

class FirebaseAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun currentUserId(): String? = auth.currentUser?.uid
    fun currentUserEmail(): String = auth.currentUser?.email.orEmpty()
    fun currentUserName(): String = auth.currentUser?.displayName.orEmpty()

    fun register(displayName: String, email: String, password: String, callback: AuthCallback) {
        val cleanName = displayName.trim()
        val cleanEmail = email.trim().lowercase(Locale.US)
        if (cleanName.isEmpty() || cleanEmail.isEmpty() || password.isEmpty()) {
            callback.onError("Please complete all required fields")
            return
        }

        auth.createUserWithEmailAndPassword(cleanEmail, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    callback.onError("Account created, but no user session was returned.")
                    return@addOnSuccessListener
                }
                val request = UserProfileChangeRequest.Builder()
                    .setDisplayName(cleanName)
                    .build()
                user.updateProfile(request)
                    .addOnCompleteListener { callback.onSuccess(user.uid, cleanEmail, cleanName) }
            }
            .addOnFailureListener { callback.onError(it.message ?: "Firebase authentication failed") }
    }

    fun signIn(email: String, password: String, callback: AuthCallback) {
        val cleanEmail = email.trim().lowercase(Locale.US)
        if (cleanEmail.isEmpty() || password.isEmpty()) {
            callback.onError("Please enter your email and password")
            return
        }

        auth.signInWithEmailAndPassword(cleanEmail, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    callback.onError("Login failed. Check your details and try again.")
                } else {
                    callback.onSuccess(user.uid, user.email.orEmpty(), user.displayName.orEmpty())
                }
            }
            .addOnFailureListener { callback.onError("Account doesn't exist") }
    }

    fun sendPasswordReset(email: String, callback: PasswordResetCallback) {
        val cleanEmail = email.trim().lowercase(Locale.US)
        if (cleanEmail.isEmpty()) {
            callback.onError("Please enter your email address first")
            return
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            callback.onError("Please enter a valid email address")
            return
        }

        auth.sendPasswordResetEmail(cleanEmail)
            .addOnSuccessListener { callback.onSuccess("Password reset email sent") }
            .addOnFailureListener { callback.onError(it.message ?: "Password reset email could not be sent") }
    }

    fun signInWithGoogle(idToken: String, callback: GoogleAuthCallback) {
        if (idToken.isBlank()) {
            callback.onError("Google sign-in did not return a token")
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    callback.onError("Google sign-in failed. Try again.")
                } else {
                    callback.onSuccess(
                        user.uid,
                        user.email.orEmpty(),
                        user.displayName.orEmpty(),
                        result.additionalUserInfo?.isNewUser == true,
                    )
                }
            }
            .addOnFailureListener { callback.onError(it.message ?: "Google sign-in failed") }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String, callback: PasswordCallback) {
        val user = auth.currentUser
        val email = user?.email?.trim()?.lowercase(Locale.US).orEmpty()
        if (user == null || email.isEmpty()) {
            callback.onError("Please sign in again before changing your password")
            return
        }
        if (currentPassword.isEmpty()) {
            callback.onError("Please enter your current password")
            return
        }
        if (newPassword.isEmpty()) {
            callback.onError("Please enter a new password")
            return
        }
        if (confirmPassword.isEmpty()) {
            callback.onError("Please confirm your new password")
            return
        }
        if (newPassword != confirmPassword) {
            callback.onError("New passwords do not match")
            return
        }
        if (newPassword.length < 6) {
            callback.onError("Password must be at least 6 characters")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { callback.onSuccess("Password changed successfully") }
                    .addOnFailureListener { callback.onError(it.message ?: "Could not change password") }
            }
            .addOnFailureListener { callback.onError("Current password is incorrect") }
    }

    fun updateDisplayName(displayName: String, callback: ProfileCallback) {
        val cleanName = displayName.trim()
        val user = auth.currentUser
        if (user == null) {
            callback.onError("Please sign in again before updating your profile")
            return
        }
        if (cleanName.isEmpty()) {
            callback.onError("Display name cannot be empty")
            return
        }

        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(cleanName)
            .build()
        user.updateProfile(request)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { callback.onError(it.message ?: "Profile could not be updated") }
    }

    fun signOut() {
        auth.signOut()
    }

    fun deleteCurrentUser(callback: DeleteAccountCallback) {
        val user = auth.currentUser
        if (user == null) {
            callback.onError("Please sign in again before deleting your account")
            return
        }

        user.delete()
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener {
                callback.onError(it.message ?: "Account could not be deleted. Please sign in again and try once more.")
            }
    }

    interface AuthCallback {
        fun onSuccess(userId: String, email: String, displayName: String)
        fun onError(message: String)
    }

    interface GoogleAuthCallback {
        fun onSuccess(userId: String, email: String, displayName: String, isNewUser: Boolean)
        fun onError(message: String)
    }

    interface PasswordCallback {
        fun onSuccess(message: String)
        fun onError(message: String)
    }

    interface PasswordResetCallback {
        fun onSuccess(message: String)
        fun onError(message: String)
    }

    interface ProfileCallback {
        fun onSuccess()
        fun onError(message: String)
    }

    interface DeleteAccountCallback {
        fun onSuccess()
        fun onError(message: String)
    }
}
