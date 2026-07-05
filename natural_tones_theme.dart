import 'package:flutter/material.dart';

/// Natural Tones Theme Configuration
///
/// An organic, premium fintech aesthetic featuring:
/// - Sage Green (#8F9779) - Balanced, calming secondary accents
/// - Terracotta (#C89679) - Earthy, warm interactive components and highlight accents
/// - Sand (#E3DCCF) - Warm, luxurious linen-like backgrounds
/// - Deep Forest Green (#2F4F4F) - Stable, high-contrast primary headers and text
class NaturalTonesTheme {
  // Brand Color Constants
  static const Color sageGreen = Color(0xFF8F9779);
  static const Color terracotta = Color(0xFFC89679);
  static const Color sand = Color(0xFFE3DCCF);
  static const Color sandLight = Color(0xFFF9F8F6);
  static const Color deepForestGreen = Color(0xFF2F4F4F);
  
  // Neutral Constants
  static const Color slateDark = Color(0xFF1E293B);
  static const Color slateLight = Color(0xFF64748B);
  static const Color borderSoft = Color(0xFFE2E8F0);

  /// Returns the complete Light [ThemeData] using Material 3 design tokens
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      colorScheme: const ColorScheme(
        brightness: Brightness.light,
        primary: deepForestGreen,
        onPrimary: Colors.white,
        primaryContainer: sand,
        onPrimaryContainer: deepForestGreen,
        secondary: sageGreen,
        onSecondary: Colors.white,
        secondaryContainer: sandLight,
        onSecondaryContainer: slateDark,
        tertiary: terracotta,
        onTertiary: Colors.white,
        background: sandLight,
        onBackground: slateDark,
        surface: Colors.white,
        onSurface: slateDark,
        surfaceVariant: sand,
        onSurfaceVariant: slateLight,
        outline: borderSoft,
        error: Color(0xFFDC2626),
        onError: Colors.white,
      ),
      
      // Scaffolding & Backgrounds
      scaffoldBackgroundColor: sandLight,
      
      // Elegant Serif & Clean Sans Typography
      textTheme: const TextTheme(
        displayLarge: TextStyle(
          color: deepForestGreen,
          fontSize: 32,
          fontWeight: FontWeight.bold,
          letterSpacing: -0.5,
        ),
        headlineMedium: TextStyle(
          color: deepForestGreen,
          fontSize: 24,
          fontWeight: FontWeight.bold,
          letterSpacing: -0.2,
        ),
        titleLarge: TextStyle(
          color: slateDark,
          fontSize: 20,
          fontWeight: FontWeight.w600,
        ),
        bodyLarge: TextStyle(
          color: slateDark,
          fontSize: 16,
          fontWeight: FontWeight.normal,
          height: 1.5,
        ),
        bodyMedium: TextStyle(
          color: slateLight,
          fontSize: 14,
          fontWeight: FontWeight.normal,
          height: 1.4,
        ),
        labelLarge: TextStyle(
          color: sageGreen,
          fontSize: 12,
          fontWeight: FontWeight.bold,
          letterSpacing: 1.1,
        ),
      ),
      
      // Modern Filled & Outlined Buttons
      buttonTheme: const ButtonThemeData(
        buttonColor: deepForestGreen,
        textTheme: ButtonTextTheme.primary,
      ),
      
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: deepForestGreen,
          foregroundColor: Colors.white,
          elevation: 0,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
            letterSpacing: 0.5,
          ),
        ),
      ),
      
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: terracotta,
          side: const BorderSide(color: terracotta, width: 1.5),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
        ),
      ),
      
      // Premium Card Designs
      cardTheme: CardTheme(
        color: Colors.white,
        elevation: 0,
        margin: EdgeInsets.zero,
        shape: RoundedRectangleBorder(
          side: const BorderSide(color: borderSoft, width: 1),
          borderRadius: BorderRadius.circular(24),
        ),
      ),
      
      // Form Input Styling
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: Colors.white,
        contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 18),
        labelStyle: const TextStyle(color: slateLight, fontWeight: FontWeight.w500),
        floatingLabelStyle: const TextStyle(color: deepForestGreen, fontWeight: FontWeight.bold),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: const BorderSide(color: borderSoft, width: 1),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: const BorderSide(color: borderSoft, width: 1),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: const BorderSide(color: deepForestGreen, width: 2),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: const BorderSide(color: Color(0xFFDC2626), width: 1),
        ),
      ),
      
      // Navigation Styling
      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: Colors.white,
        indicatorColor: sand,
        labelTextStyle: MaterialStateProperty.resolveWith((states) {
          if (states.contains(MaterialState.selected)) {
            return const TextStyle(color: deepForestGreen, fontWeight: FontWeight.bold, fontSize: 12);
          }
          return const TextStyle(color: slateLight, fontWeight: FontWeight.medium, fontSize: 12);
        }),
        iconTheme: MaterialStateProperty.resolveWith((states) {
          if (states.contains(MaterialState.selected)) {
            return const IconThemeData(color: deepForestGreen);
          }
          return const IconThemeData(color: slateLight);
        }),
      ),
    );
  }
}
