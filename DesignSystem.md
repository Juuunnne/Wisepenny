# Wisepenny — Design System

Implementation-ready specification for the Wisepenny mobile app. Compose Multiplatform (Android + iOS), dark-mode only, mobile viewport 390×844.

This document is the source of truth. When a screen mockup and this document disagree, the document wins.

---

## 1. Brand & principles

Wisepenny is a financial education and micro-savings app for French users aged 18–26. The visual language is **premium, trustworthy, energetic** — confident without being austere, direct without being childish.

Five rules govern every screen.

1. **One hero per screen.** A single oversized number or display title carries the screen. Everything else is supporting context.
2. **Mint is precious.** Mint appears on at most one surface per screen — either the primary CTA or one highlighted card, never both on the same surface.
3. **High contrast always.** White on near-black. Dark on mint. No light gray on white. No mid-gray on near-black for body text.
4. **Generous whitespace.** Minimum 24px between distinct sections. Cards breathe.
5. **Mobile-first, dark only.** Designed at 390×844. There is no light mode in this product.

### What we never use

No gradients. No glow effects. No neon shadows. No drop shadows of any kind. No glassmorphism. No skeuomorphism. No 3D. No pastels (except the very-light mint variant). No multiple accent colors — mint is the only brand color; semantic colors are scoped to system feedback. No childish illustrations. No emoji in interface copy. No light mode.

---

## 2. Color tokens

Exact hex values. Each token has a stable name that will become the property name in `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/theme/Color.kt`.

### Background & surface

| Token | Hex | Purpose |
|---|---|---|
| `BackgroundPrimary` | `#0A0F1A` | App background. Near-black with a slight blue tint. |
| `SurfaceElevated` | `#141B2A` | Cards, modals, bottom tab bar — anything raised above the background. |
| `BorderSubtle` | `#1F2937` | Dividers and 1px borders on dark surfaces. |

### Accent (mint)

| Token | Hex | Purpose |
|---|---|---|
| `AccentMint` | `#5EEAD4` | Primary CTAs, highlighted cards, key figures, active states. |
| `AccentMintPressed` | `#2DD4BF` | Pressed/hover state on mint surfaces. |
| `AccentMintSoft` | `#CCFBF1` | Text and icons on mint backgrounds, very subtle mint tints. |

### Text on dark

| Token | Hex | Purpose |
|---|---|---|
| `TextPrimary` | `#FFFFFF` | Body and headings on dark surfaces. |
| `TextSecondary` | `#E5E7EB` | Secondary content on dark surfaces. |
| `TextTertiary` | `#9CA3AF` | Captions, metadata, timestamps. |
| `TextDisabled` | `#6B7280` | Disabled labels only. Never use for normal text — fails AA. |

### Light cards

| Token | Hex | Purpose |
|---|---|---|
| `SurfaceLight` | `#FFFFFF` | Primary white card. |
| `SurfaceLightVariant` | `#F3F4F6` | Secondary white card variant (subtle differentiation when two white cards meet). |
| `TextOnLight` | `#0A0F1A` | Primary text on white cards. |
| `TextOnLightSecondary` | `#4B5563` | Secondary text on white cards. |

### Semantic (system feedback only)

These colors appear **only** in toasts, validation messages, status pills, and progress indicators. They are never decorative. If you reach for a semantic color outside of system feedback, you are misusing it.

| Token | Hex | Purpose |
|---|---|---|
| `Success` | `#22C55E` | Streak active, goal reached, transaction confirmed. |
| `Danger` | `#EF4444` | Destructive action, validation error. |
| `Warning` | `#F59E0B` | Budget exceeded, gentle alert. |
| `Info` | `#3B82F6` | Educational tip, informational banner. |

---

## 3. Typography

**Font family:** Inter, all weights from 400 to 800. Bundle the variable font in `composeApp/src/commonMain/composeResources/font/`.

The voice is built from two extremes: **massive bold display** for key information, **calm small body** for everything else. H1 and H2 exist for structural document use (settings sections, modal titles) but should not be the visual anchor of a screen — the anchor is always Display.

### Scale (fixed sizes — no ranges)

| Role | Size | Weight | Line-height | Letter-spacing | Use |
|---|---|---|---|---|---|
| Display XL | 56sp | 800 | 1.05 | -0.02em | Hero number on home screen, screen-defining titles. May be UPPERCASE. |
| Display L | 40sp | 800 | 1.05 | -0.01em | Section hero amounts, goal totals. |
| H1 | 28sp | 700 | 1.2 | -0.01em | Modal titles, settings section headers. |
| H2 | 20sp | 600 | 1.25 | 0 | Sub-section titles, card headers when needed. |
| Body | 16sp | 400 | 1.5 | 0 | Default reading text, list rows, descriptions. |
| Body Strong | 16sp | 600 | 1.5 | 0 | Emphasized inline text, button labels. |
| Caption | 13sp | 500 | 1.4 | 0 | Metadata, timestamps, helper text. Always `TextTertiary`. |

### Number rules

- A **hero amount** (account balance, goal total, lesson streak count) uses Display XL or Display L weight 800.
- An **inline amount** (transaction list row, small stat in a card) uses Body Strong. Do not jump to Display for non-hero numbers.
- Currency: see Section 11 (Locale).

---

## 4. Spacing

Use only these values. Anything between is wrong.

`4 · 8 · 12 · 16 · 20 · 24 · 32 · 48 · 64`

| Token | dp | Common use |
|---|---|---|
| `space1` | 4 | Tight inline gaps (icon to label). |
| `space2` | 8 | Chip padding, tight stack gaps. |
| `space3` | 12 | List row internal gaps. |
| `space4` | 16 | Default padding inside small cards, button padding. |
| `space5` | 20 | Card padding (medium). |
| `space6` | 24 | Card padding (large), minimum gap between sections. |
| `space7` | 32 | Major section separation. |
| `space8` | 48 | Screen-level separation, hero blocks. |
| `space9` | 64 | Onboarding-scale breathing room. |

Screen edge padding: **24dp** on left and right by default.

---

## 5. Radius

| Token | dp | Applies to |
|---|---|---|
| `radiusSmall` | 12 | Chips, pills, small badges, inline tags. |
| `radiusMedium` | 16 | Buttons, inputs, list rows, medium cards. |
| `radiusLarge` | 24 | Large cards, modal sheets (top corners only), feature blocks. |
| `radiusPill` | 999 | Fully rounded pills (streak day indicators, filter chips). |

---

## 6. Elevation

**Wisepenny does not use shadows.** Elevation is expressed through background color contrast.

| Level | Mechanism |
|---|---|
| Base | `BackgroundPrimary` (`#0A0F1A`). |
| Raised | `SurfaceElevated` (`#141B2A`) over base. |
| Floating | `SurfaceLight` (`#FFFFFF`) over base, with `radiusLarge`. The brightness step is the elevation cue. |
| Modal | Scrim `#0A0F1A` at 60% opacity over the underlying screen. The sheet itself uses `SurfaceLight` or `SurfaceElevated`. |

The scrim is the **only** semi-transparent layer in the system. Nothing else uses opacity for stacking.

---

## 7. Motion

Motion is what makes "energetic" perceptible without resorting to color or decoration.

### Durations

| Token | Value | Use |
|---|---|---|
| `motionFast` | 150ms | Press feedback, micro-state changes (toggle, ripple substitute). |
| `motionBase` | 250ms | Default transition: tab switches, card expansion, modal open. |
| `motionSlow` | 400ms | Hero number count-up animations, large layout transitions, onboarding reveals. |

### Easing

Default easing curve: `cubic-bezier(0.2, 0.0, 0.0, 1.0)` — a fast-out, slow-in curve that feels confident. In Compose: `CubicBezierEasing(0.2f, 0f, 0f, 1f)`. Use this everywhere unless a specific interaction demands otherwise.

### Patterns

- **Stagger:** when revealing a list (e.g., a lesson grid or transaction list), stagger children at 40ms intervals up to a maximum of 8 items. Beyond 8, fade the whole group as one.
- **Number transitions:** balances and totals count up over `motionSlow` using interpolated digits. Never instant-swap a hero number.
- **Page transitions:** horizontal slide at `motionBase` for forward navigation, fade at `motionFast` for tab switches.
- **No bounce.** No spring overshoot. The brand is confident, not playful-bouncy.

---

## 8. Iconography

- **Style:** outline, 1.5px stroke, rounded line caps. Filled variants only for active navigation tabs and primary CTAs.
- **Sizes:** 20dp inline (in list rows), 24dp navigation, 32dp+ feature illustration.
- **Recommended set:** Lucide Icons. Open-source, consistent stroke, available as a Compose Multiplatform port. Bundle only the icons in use.
- **Color:** icons inherit text color of their context. Active nav icons use `AccentMint`. Inactive nav icons use `TextTertiary`.

No emoji ever appears in production UI text.

---

## 9. Components

Every component below lists: purpose, anatomy, sizes, states, accessibility notes.

### 9.1 Buttons

**Primary**
- Purpose: the single most important action on a screen.
- Anatomy: mint background `AccentMint`, dark label `TextOnLight`, optional right-aligned arrow icon (↗ outline, 20dp).
- Size: full-width by default, 56dp height, `radiusMedium`, padding 16dp vertical / 24dp horizontal.
- Label: Body Strong (16sp / 600).
- States:
  - Default: `AccentMint` background.
  - Pressed: `AccentMintPressed` background, no scale change.
  - Disabled: `SurfaceElevated` background, `TextDisabled` label, no icon.
  - Loading: replace label with a 20dp circular indicator in `TextOnLight`.
- A11y: 48dp minimum touch target (met at 56dp). Label must describe the action ("Épargner 5 €", not "Continuer").

**Secondary**
- Purpose: alternative or competing action on the same screen.
- Anatomy: transparent background, 1.5px `TextPrimary` border, `TextPrimary` label.
- Size and states: same dimensions as primary; pressed state fills with `SurfaceElevated`.

**Tertiary**
- Purpose: low-priority navigation or dismissal ("Plus tard", "Voir tout").
- Anatomy: text-only, `AccentMint` color, no background, no border.
- Size: 44dp touch target via padding, label Body Strong.

### 9.2 Inputs

- Anatomy: `SurfaceElevated` background, 1.5px `BorderSubtle` border, 16dp internal padding, `radiusMedium`, label above input in Caption color `TextTertiary`.
- Text: Body, `TextPrimary`.
- Placeholder: Body, `TextTertiary`.
- Helper text below: Caption, `TextTertiary`.
- Height: 56dp.
- States:
  - Default: as above.
  - Focused: border becomes `AccentMint`, no glow.
  - Filled (with value): identical to default.
  - Error: border `Danger`, helper text `Danger`.
  - Disabled: background `BackgroundPrimary`, label `TextDisabled`.
- A11y: every input has a visible label (not placeholder-as-label).

### 9.3 Cards

Three card types. Pick based on role, not aesthetics.

**Light card (`SurfaceLight`)**
- Purpose: primary content the user reads or acts upon. Most informational cards.
- Padding: 24dp, `radiusLarge`.
- Text: `TextOnLight` / `TextOnLightSecondary`.

**Dark elevated card (`SurfaceElevated`)**
- Purpose: secondary or supporting content, list groupings, settings rows.
- Padding: 20dp, `radiusLarge`.
- Text: `TextPrimary` / `TextSecondary`.

**Mint highlight card (`AccentMint`)**
- Purpose: the single most important card on the screen. At most one per screen.
- Padding: 24dp, `radiusLarge`.
- Text: `TextOnLight` (dark, for contrast). Numbers in Display L weight 800.

**Card-alternation rule:** on a vertical scroll, alternate light and dark-elevated cards so the visual rhythm is clear. Light = act-on-this. Dark = context. Aim for no more than ~50/50 split per screen. Never stack three light cards in a row.

### 9.4 Bottom navigation

- Container: `SurfaceElevated`, height 64dp + bottom safe area inset, no top border.
- 4 or 5 destinations. Each tab is 56dp wide minimum.
- Icon: 24dp outline (inactive) or filled (active).
- Label: Caption (13sp / 500) below icon, optional. If used, must be used on all tabs.
- Active: `AccentMint` icon and label. Inactive: `TextTertiary`.
- Active indicator: an 8dp wide × 3dp tall mint pill, 4dp above the icon.

### 9.5 Top app bar

- Height: 56dp + status bar inset.
- Background: `BackgroundPrimary` (transparent over hero screens).
- Title: H2 size, weight 500 (not 600 — bars are quiet), centered or leading.
- Trailing action: a single 24dp outline icon, optional.
- No back button on root tabs. Back button on nested screens: 24dp chevron-left, `TextPrimary`.

### 9.6 Modal sheets

**Partial sheet** (default)
- Anchored bottom, `SurfaceElevated`, top corners `radiusLarge`.
- Drag handle: 4dp tall × 36dp wide pill, `BorderSubtle`, 8dp from top.
- Padding: 24dp horizontal, 24dp top, 24dp + safe area bottom.
- Scrim: `BackgroundPrimary` at 60% opacity over background.
- Enter: slide up at `motionBase`. Exit: slide down at `motionBase`.

**Full sheet**
- Full-screen, top corners `radiusLarge` (peek behind on iOS) or square (Android).
- Status bar: leaves a 12dp gap at top showing previous screen.
- Same scrim and motion.

### 9.7 Toasts and snackbars

- Position: bottom, 24dp above safe area or above bottom nav if present.
- Container: `SurfaceLight`, `radiusMedium`, 16dp padding, max width 358dp (screen width minus 16dp on each side).
- Text: Body, `TextOnLight`.
- Optional 20dp leading icon — `Success` for confirmation, `Danger` for error, `Info` for tips.
- Duration: 4 seconds default. 6 seconds if it contains an action.
- Action (optional): tertiary button style, mint, right-aligned.
- Enter: slide up + fade at `motionBase`. Exit: fade only at `motionFast`.

### 9.8 List rows

Two row patterns. Use whichever fits content density.

**Transaction-style row** (compact)
- Height: 64dp.
- Layout: 40dp circular icon (leading), title + caption stacked, amount (Body Strong) trailing.
- Divider: 1dp `BorderSubtle` between rows, indented to align with text.

**Lesson-style row** (rich)
- Height: 88dp.
- Layout: 56dp icon or thumbnail, title (Body Strong) + subtitle (Caption) + optional progress bar, trailing 16dp chevron in `TextTertiary`.
- No divider; rows are visually separated by gap (12dp).

### 9.9 Progress, streak, badges

**Progress bar**
- Height: 8dp, `radiusSmall` (4dp).
- Track: `BorderSubtle`.
- Fill: `AccentMint`.
- Animate fill changes over `motionBase`.

**Streak indicator**
- Row of 7 circles, 12dp diameter, 8dp gap.
- Completed day: `AccentMint` filled.
- Today (pending): `AccentMint` outline 1.5px.
- Future day: `BorderSubtle` filled.

**Badge**
- Circular, 64dp diameter.
- Earned: `AccentMint` background, dark icon.
- Locked: `SurfaceElevated` background, `TextTertiary` icon.
- Label: Caption centered below, 8dp gap.

### 9.10 Charts

Charts are deliberately minimal. They communicate trend, not precision.

**Savings line chart**
- 1.5dp stroke, `AccentMint`.
- No fill under line. No gridlines. No axis labels.
- Below the chart: two captions — start value (left) and current value (right), both Body Strong.
- Optional: a single dot marker on the current point, 8dp `AccentMint` filled.
- Height: 120dp.

**Goal donut**
- Track: `BorderSubtle`, stroke width 12dp.
- Fill: `AccentMint`, same stroke, animated over `motionSlow` on first render.
- Center: percentage in Display L weight 800, no decimal.

### 9.11 Empty, loading, error states

**Empty state**
- Centered vertical stack: 80dp geometric placeholder (simple shape outline in `TextTertiary`, no illustration), 24dp gap, H2 title, 8dp gap, Body Caption description, 24dp gap, primary or tertiary action.

**Loading state — skeleton**
- Use skeleton blocks of `SurfaceElevated`, matching the final layout's shape.
- Animate with a horizontal shimmer across the block from -50% to 150% over 1200ms, looping with a 400ms pause.
- Skeleton blocks are `radiusMedium`.

**Error state**
- Same anatomy as empty state. Title uses `Danger` color. Description in `TextSecondary`. Primary action is "Réessayer".

---

## 10. Accessibility

All text/background pairs in active use have been verified against WCAG 2.1. Target: 4.5:1 for normal text, 3:1 for large text (≥24sp) and UI components.

### Verified contrast ratios

| Foreground | Background | Ratio | Status |
|---|---|---|---|
| `TextPrimary` (`#FFFFFF`) | `BackgroundPrimary` (`#0A0F1A`) | 19.2:1 | AAA |
| `TextPrimary` | `SurfaceElevated` (`#141B2A`) | 17.2:1 | AAA |
| `TextSecondary` (`#E5E7EB`) | `BackgroundPrimary` | 15.5:1 | AAA |
| `TextTertiary` (`#9CA3AF`) | `BackgroundPrimary` | 7.6:1 | AAA |
| `TextDisabled` (`#6B7280`) | `BackgroundPrimary` | 4.0:1 | AA large/UI only — never normal text |
| `AccentMint` (`#5EEAD4`) | `BackgroundPrimary` | 13.0:1 | AAA |
| `TextOnLight` (`#0A0F1A`) | `SurfaceLight` (`#FFFFFF`) | 19.2:1 | AAA |
| `TextOnLight` | `AccentMint` | 13.0:1 | AAA |
| `TextOnLightSecondary` (`#4B5563`) | `SurfaceLight` | 7.6:1 | AAA |
| `TextOnLightSecondary` | `SurfaceLightVariant` (`#F3F4F6`) | 6.8:1 | AAA |
| `Success` (`#22C55E`) | `BackgroundPrimary` | 8.4:1 | AAA |
| `Danger` (`#EF4444`) | `BackgroundPrimary` | 5.1:1 | AA |
| `Warning` (`#F59E0B`) | `BackgroundPrimary` | 8.9:1 | AAA |
| `Info` (`#3B82F6`) | `BackgroundPrimary` | 5.2:1 | AA |
| `BorderSubtle` (`#1F2937`) | `BackgroundPrimary` | 1.3:1 | Non-text dividers only |

### Other requirements

- **Touch targets:** 48dp minimum on all interactive elements. Where visual size is smaller (small icons, chips), extend hit area with padding.
- **Focus indicators:** for keyboard navigation (Android TV, hardware keyboards, accessibility services), focused elements draw a 2dp outer `AccentMint` ring with 4dp offset.
- **Reduce motion:** when the OS reports "Reduce Motion" enabled, replace slides with fades at `motionFast`, disable number count-up (show final value immediately), disable skeleton shimmer (keep static skeleton).
- **Dynamic type:** support OS font scaling up to 130%. Hero numbers may stay fixed (mark as `nonScaledFontSize`) to preserve layout; all body text scales.
- **Color is never the only signal.** Streak completion uses both color and a filled/outline distinction. Errors use both color and an icon.

---

## 11. Locale (French)

The product is French-first. Implementation must follow French formatting conventions.

### Currency

- Format: `1 234,56 €` — space-separated thousands (non-breaking space ` `), comma decimal, euro sign **after** the number with a non-breaking space.
- Negative: `-1 234,56 €` (sign before, no parentheses).
- No-cents amounts: `25 €` (omit `,00` when amount is whole). Use `25,00 €` only in detailed transaction views.

### Numbers

- Thousands separator: non-breaking space.
- Decimal separator: comma.
- Percentage: `42 %` (with non-breaking space before `%`).
- Date: `lun. 18 mai` (short), `lundi 18 mai 2026` (long). Lowercase day/month names per French convention.

### Copy length budget

French copy runs ~20% longer than English. Implications:
- Button labels: target ≤20 characters, hard max 28. Wrap to two lines only as a last resort.
- Card titles: ≤32 characters before truncation.
- Caption text: allow up to 3 lines before ellipsis.

### Typography accents

Inter has full Latin Extended coverage. Verify rendering of `é à è ê ô ç î ï ù û` at every size during QA.

---

## 12. Platform notes (Compose Multiplatform)

This is a Kotlin Multiplatform project with shared Compose UI in `composeApp/` and an iOS app shell in `iosApp/`. The same tokens render to both platforms.

### Fonts

- Bundle the Inter variable font (`Inter-VariableFont_slnt,wght.ttf`) at `composeApp/src/commonMain/composeResources/font/`.
- Reference it in `Type.kt` via `Font(Res.font.inter, ...)`.
- Do **not** rely on system fonts. Android defaults to Roboto and iOS to SF Pro — both will silently break the brand.

### Ripple and press feedback

- Material's default ripple is too noisy for this brand. Replace `LocalIndication` with a custom indication that simply darkens the surface to `AccentMintPressed` (on mint) or 8% white overlay (on dark surfaces) for `motionFast` duration.
- Configure once at the root of the theme.

### System bars

- Status bar: transparent, `lightStatusBarIcons = false` (white icons over dark background).
- Navigation bar (Android): `BackgroundPrimary`, light icons.
- Edge-to-edge: enabled. The bottom tab bar extends behind the gesture handle area; padding is added via `WindowInsets.navigationBars`.
- iPhone home indicator: the bottom tab bar's safe-area padding hosts a `SurfaceElevated` extension behind the indicator.

### Safe areas

- Use `WindowInsets.safeDrawing` for the root screen padding.
- Top app bar adds `WindowInsets.statusBars` to its own height.
- Bottom nav adds `WindowInsets.navigationBars` to its own height.

### Font scaling

Compose respects `LocalDensity` font scaling on both platforms. To prevent hero numbers from breaking layout at 130% scale, use `TextUnit.Sp` for body text and a fixed `sp` with `style = TextStyle(fontSize = ... , lineHeight = ...)` plus `textAutoSize` constraints (when available) on display numbers.

---

## 13. Decisions log

These resolve contradictions and ambiguities from the original design brief.

| Topic | Original | Resolution |
|---|---|---|
| Display size | "48–64px" range | Fixed: Display XL = 56sp, Display L = 40sp. |
| H1 size | "24–28px" range | Fixed: H1 = 28sp. |
| "Avoid medium headings" | Contradicted H1/H2 existence | Re-scoped: no decorative headings between H1 (28sp) and Display L (40sp). H1/H2 exist for structural use (modal titles, sub-sections) but never carry the visual weight of a screen — that role belongs to Display only. |
| "Mint only" vs four semantic colors | Apparent conflict | Resolved: mint is the only **brand** accent. Semantic colors (`Success`/`Danger`/`Warning`/`Info`) are permitted **only** for system feedback (toasts, validation, status pills, progress indicators tied to state) and never as decorative accents. |
| Inline numbers in lists | "Numbers always Display 800" | Re-scoped: **hero** numbers use Display weight 800. Inline numbers in list rows and small stats use Body Strong (16sp / 600). |
| Mint per screen | "1–2 mint elements" | Tightened: at most one mint **surface** per screen (the CTA or a highlighted card, not both). A mint icon in a row is not a mint surface. |
| Card alternation | "Alternate for rhythm" | Clarified: light cards = primary act-on content; dark-elevated cards = supporting context. Aim ~50/50 per screen, never three light cards stacked. |
| Shadows | Not specified | Banned outright. Elevation expressed via background color step. The only semi-transparent layer is the modal scrim (60% `BackgroundPrimary`). |
| Motion | Not specified | Added: `motionFast` 150ms / `motionBase` 250ms / `motionSlow` 400ms with `cubic-bezier(0.2, 0, 0, 1)` default easing. No spring overshoot. |
| Line-heights | Body only specified | Added: display 1.05, headings 1.2, body 1.5. |
| Disabled text | `#6B7280` listed under neutrals | Reclassified as `TextDisabled` only. Fails AA for normal text — must not be used for non-disabled content. |
| Border `#1F2937` | "Subtle border on dark" | Confirmed for dividers only. Contrast 1.3:1 means it is intentionally near-invisible — present as structural hint, not a visible line on OLED. |

---

## 14. Implementation hints

Token groups map to these files (to be created in a follow-up implementation task):

| Concern | Path |
|---|---|
| Color tokens | `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/theme/Color.kt` |
| Typography | `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/theme/Type.kt` |
| Spacing | `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/theme/Spacing.kt` |
| Radius / shapes | `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/theme/Shape.kt` |
| Motion durations & easing | `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/theme/Motion.kt` |
| Theme assembly + custom ripple | `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/theme/Theme.kt` |
| Inter font asset | `composeApp/src/commonMain/composeResources/font/` |
| Custom components | `composeApp/src/commonMain/kotlin/com/wisepenny/presentation/components/` |

Tokens are accessed via a `WisepennyTheme` composable wrapper exposing `WisepennyTheme.colors`, `WisepennyTheme.typography`, `WisepennyTheme.spacing`, `WisepennyTheme.radius`, `WisepennyTheme.motion`. Do not reference Material `MaterialTheme` from feature code.
