package com.proofloop.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ProofLoop Neo-Brutalist Color Palette (v3.0 - High Contrast Minimalist Edition).
 *
 * Designed with Swiss modernist principles:
 * - Architectural Chalk canvas for eye-comfort and tactile paper warmth
 * - Deep Obsidian Jet-Black for razor-sharp 2dp borders and crisp unblurred drop shadows
 * - Electric Klein Blue & Volt Citron as high-energy signature brand accents
 * - Signal Emerald, Solar Tangerine, Digital Lilac, and Hot Rose for semantic clarity
 */

// --- 1. Base Structural Tokens ---
val ProofObsidianInk = Color(0xFF0C0C0E)        // Deep Obsidian Jet-Black (#0C0C0E)
val ProofArchitecturalPaper = Color(0xFFF4F0EA) // Architectural Warm Chalk (#F4F0EA)
val ProofPaperLight = Color(0xFFFAF8F5)        // Ultra-light surface paper (#FAF8F5)
val ProofPureWhite = Color(0xFFFFFFFF)         // Pure Stark White (#FFFFFF)
val ProofRecessedMuted = Color(0xFFE8E2D5)     // Recessed track & inset border (#E8E2D5)
val ProofCharcoalMuted = Color(0xFF555260)     // High-legibility muted body text (#555260)

// --- 2. High-Energy Electric Accents ---
val ProofKleinBlue = Color(0xFF1E3AFA)         // International Klein Electric Blue (#1E3AFA)
val ProofVoltCitron = Color(0xFFCCFF00)        // Hyper Cyber Volt Citron (#CCFF00)
val ProofSignalEmerald = Color(0xFF00E599)     // Signal Emerald / Verified Jade (#00E599)
val ProofSolarTangerine = Color(0xFFFF542E)    // Solar Tangerine / Neo Flame (#FF542E)
val ProofDigitalLilac = Color(0xFFC084FC)      // Digital Lilac / Prism Violet (#C084FC)
val ProofHotRose = Color(0xFFFF3385)           // Hot Rose / Cyber Flamingo (#FF3385)

// --- 3. Core Semantic Mappings (Full backward & cross-screen compatibility) ---
val ProofMidnight = ProofObsidianInk
val ProofInk = ProofObsidianInk
val ProofBorder = ProofObsidianInk
val ProofPaper = ProofArchitecturalPaper
val ProofWhite = ProofPureWhite
val ProofSurfaceMuted = ProofRecessedMuted
val ProofTextMuted = ProofCharcoalMuted

// Primary Brand Actions & Highlights
val ProofCobalt = ProofKleinBlue               // Primary hero CTA & active indicators (#1E3AFA)
val ProofAcidLime = ProofVoltCitron            // Volt Citron pop on badges & buttons (#CCFF00)
val ProofSignalMint = ProofSignalEmerald       // Verified 2-party outcomes & milestones (#00E599)
val ProofElectricLilac = ProofDigitalLilac     // Secondary highlights & blueprints (#C084FC)
val ProofLavender = ProofDigitalLilac          // Tag pill accents (#C084FC)
val ProofCoral = ProofSolarTangerine           // Attention, urgent health alerts (#FF542E)

// Dark/Elevated Surface Accents
val ProofPlumSurface = Color(0xFF201E2E)       // Deep elevated dark surface
val ProofPlum = ProofPlumSurface
val ProofRaisedPlum = ProofPlumSurface
val ProofMutedLavender = Color(0xFFE2D6FF)     // Soft pastel lilac container (#E2D6FF)

