# رکشہ کرش: شاہراہِ پاکستان (Rickshaw Crush)
### 100% Native Open-Source Android Mobile App (Candy Crush Saga Alternative)

یہ ایک مکمل **اوپن سورس اینڈرائیڈ موبائل ایپ (Native Android Mobile Game)** ہے جو کینڈی کرش ساگا (Candy Crush Saga) کے ایک ایک مکینزم، ڈائنامکس، لاجک، فزکس اور کمبوز پر مبنی ہے، لیکن اسے **خالص پاکستانی کلچر اور ٹریفک تھیم** میں ڈھالا گیا ہے۔

---

## 🎨 پاکستانی گاڑیاں اور کینڈیز کا متبادل (Vehicle Equivalents)

| کینڈی کرش ساگا (Original) | رکشہ کرش (Rickshaw Crush) | رنگ و ڈیزائن |
| :--- | :--- | :--- |
| **Green Candy** | **سبز رکشہ (Green Rickshaw)** | سبز باڈی، پیلی چھت اور ہیڈ لائٹ |
| **Red Candy** | **سجی ہوئی بس (Truck Art Bus)** | روایتی پاکستانی ٹرک آرٹ ڈیزائن، لال رنگ |
| **Orange Candy** | **موٹر سائیکل 70 (CD-70 Motorbike)** | نارنجی/سرخ پٹرول ٹینک، کروم ہینڈل بار |
| **Blue Candy** | **چنگ چی رکشہ (Qingqi / Loader)** | نیلا فریم، مسافر سیٹیں اور سائبان |
| **Yellow Candy** | **ٹیکسی (Yellow Cab)** | پیلی ٹیکسی، کالی پٹی اور ٹیکسی بورڈ |
| **White Candy** | **مہران باس (White Mehran 800cc)** | سلور/سفید باڈی، کلاسک گرل اور مربع لائٹس |

---

## ⚡ اسپیشل آئٹمز اور پاور اپس (Special Items & Combos)

1. **لائن کلیئر - ایمبولینس پروٹوکول (Striped Vehicle - 4 Match)**:
   - جب 4 گاڑیاں قطار میں ملائی جاتی ہیں تو ایمبولینس / وی آئی پی سائرن والی گاڑی بنتی ہے۔
   - یہ چلنے پر پوری سڑک (پوری قطار یا کالم) کو فوراً کلیئر کر دیتی ہے۔

2. **پریشر ہارن بلاسٹ (Wrapped Vehicle - 5 Match in L/T Shape)**:
   - جب 5 گاڑیاں L یا T شکل میں ملتی ہیں تو پریشر ہارن گاڑی بنتی ہے۔
   - یہ ارد گرد کے 3x3 دائرے میں دھماکہ کر کے ٹریفک کلیئر کرتی ہے اور نیچے گر کر دوبارہ دھماکہ کرتی ہے۔

3. **ٹریفک وارڈن سگنل (Traffic Signal Color Bomb - 5 Match in Straight Line)**:
   - سیدھی لائن میں 5 گاڑیاں ملانے پر ملٹی کلر ٹریفک سگنل بنتا ہے۔
   - اسے جس گاڑی کے ساتھ تبدیل کیا جائے، بورڈ پر موجود اس قسم کی تمام گاڑیاں لیزر لائٹ کے ساتھ ختم ہو جاتی ہیں!

4. **سپر کمبوز (Super Combos)**:
   - **سگنل + سگنل**: پوری شاہراہ سے تمام گاڑیاں فوری کلیئر ہو جاتی ہیں (Full Board Wipe)!
   - **سگنل + سٹرائپڈ**: اس رنگ کی تمام گاڑیاں سٹرائپڈ ایمبولینس بن کر بیک وقت چل پڑتی ہیں!
   - **سٹرائپڈ + ریپڈ**: 3 پوری قطاریں اور 3 پورے کالم ایک ساتھ صاف کرنے والی میگا بیم!

---

## 🔊 روایتی آوازیں اور اردو تحسین (Sounds & Urdu Cheer)

- **رکشہ ہارن**: تیز "پیپ پیپ" ہارن۔
- **بس پریشر ہارن**: روایتی پوم پوم پریشر ہارن۔
- **اردو بینرز**:
  - کمبو 1: **"واہ بھائی واہ!"**
  - کمبو 2: **"کمال کر دیا!"**
  - کمبو 3: **"شاباش اوئے!"**
  - بڑا کمبو: **"زبردست جانی! مہران اوورٹیک!"**
  - جیت: **"مبارک ہو! شاہراہ رواں دواں!"**
  - ہار: **"ٹریفک جام ہو گیا!"**

---

## 📱 اینڈرائیڈ موبائل ایپ کا سٹرکچر (Android Kotlin Architecture)

```
RickshawCrush-Android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/pakistan/rickshawcrush/
│       │   ├── MainActivity.kt               # مین اینڈرائیڈ اسکرین اور ڈائیلاگز
│       │   ├── model/
│       │   │   ├── VehicleType.kt           # تمام 6 پاکستانی گاڑیاں
│       │   │   ├── SpecialType.kt           # اسپیشل پاور اپس
│       │   │   ├── Tile.kt                  # گرڈ ٹائل ڈیٹا ماڈل
│       │   │   └── MatchResult.kt           # کمبو اور میچ گروپس
│       │   ├── engine/
│       │   │   ├── BoardManager.kt          # 8x8 میچ 3، فزکس اور کشش ثقل لاجک
│       │   │   ├── SoundFXEngine.kt         # آڈیو ٹریک ساؤنڈ سنتھیسائزر
│       │   │   └── ParticleEngine.kt        # سپارکس، لیزر اور بلاسٹ پارٹیکلز
│       │   └── ui/
│       │       ├── RickshawGameView.kt      # اینڈرائیڈ سرفیس ویو 60fps گیم لوپ
│       │       └── RickshawRenderer.kt      # ویکٹر گرافکس کینوس رینڈرر
│       └── res/
│           ├── layout/activity_main.xml     # اردو ٹرک آرٹ ڈیزائن لے آؤٹ
│           ├── values/strings.xml           # انگریزی سٹرنگز
│           └── values-ur/strings.xml        # خالص اردو لوکلائزیشن
```

---

## 🚀 APK کیسے بنائیں اور موبائل پر چلائیں (How to Build APK)

### طریقہ 1: اینڈرائیڈ اسٹوڈیو کے ذریعے (Android Studio)
1. **Android Studio** کھولیں اور `Open an Existing Project` منتخب کریں۔
2. `D:\Candy\RickshawCrush-Android` کا فولڈر سلیکٹ کریں۔
3. مینیو سے **Build > Build Bundle(s) / APK(s) > Build APK(s)** پر کلک کریں۔
4. آپ کا تیار شدہ APK یہاں ملے گا:
   `RickshawCrush-Android\app\build\outputs\apk\debug\app-debug.apk`

### طریقہ 2: کمانڈ لائن (Terminal / Gradle)
```bash
cd D:\Candy\RickshawCrush-Android
gradlew assembleDebug
```

### طریقہ 3: موبائل یا ایمولیٹر (LDPlayer) پر انسٹال کرنا
```bash
adb install app-debug.apk
```
