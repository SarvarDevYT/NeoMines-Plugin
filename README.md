# ⛏️ NeoMines - Advanced Mine Management Plugin

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21+-brightgreen.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur%20%7C%20Spigot-blue.svg)](https://papermc.io/)

**NeoMines** — Minecraft 1.21+ (Paper, Purpur, Spigot) serverlari uchun mo'ljallangan zamonaviy, kuchli va to'liq interaktiv GUI menyulariga ega bo'lgan shaxtalarni boshqarish plagini.

100% o'zbek tilidagi qo'llab-quvvatlash, avtomatik va foizli reset tizimi, maxsus drop va Fortune imkoniyatlari, Actionbar ogohlantirishlari va PlaceholderAPI integratsiyasi.

---

## 🌟 Asosiy Imkoniyatlar

- **13 ta Interaktiv GUI menyulari:** Barcha sozlamalar chat buyruqlarisiz ham vizual menyular orqali boshqariladi.
- **3 xil Yangilanish Rejimi:**
  - `TIME` (Vaqt bo'yicha orqaga sanash)
  - `PERCENTAGE` (Qazilgan bloklar foizi bo'yicha)
  - `TIME_PERCENTAGE` (Ikkala rejim birgalikda)
- **Ogohlantirish Tizimi (Warning System):**
  - Chat orqali global yoki ma'lum masofadagi o'yinchilarga xabar
  - Paper Adventure Component orqali o'yinchilarning Action bar / Hotbar qismida taymer
  - Belgilangan soniyalarda (masalan, 600, 300, 60, 20, 5 soniya) xabar berish
- **Xavfsiz Reset (Anti-Stuck):** Shaxta yangilangan paytda ichidagi o'yinchilarni shaxta tepasiga yoki maxsus belgilangan teleport nuqtasiga ko'chirish.
- **Minimal Pickaxe Efficiency Cheklovi:** Shaxtadagi bloklarni qazish uchun minimal pickaxe darajasini talab qilish (`neomines.break` bypass ruxsatnomasi mavjud).
- **Custom Drop va Fortune:** Har bir blokdan standart drop o'rniga maxsus buyumlar tushishi va Fortune (Omad) ko'paytirgichini sozlash.
- **Ko'p tillilik (Multi-Language):** Birlamchi til sifatida O'zbek tili (`UZ`), shuningdek `EN`, `ES`, `CN` va `CUSTOM`.
- **PlaceholderAPI Integratsiyasi:** Scoreboard, tab va xabarlar uchun boy `%neomines_...%` placeholderlari.

---

## 📋 Buyruqlar va Ruxsatlar

Asosiy buyruq: `/neomines`  
Qisqa aliaslar: `/nm`, `/nmine`, `/nmines`, `/neomomine`

| Buyruq | Tavsif | Ruxsat (Permission) |
| :--- | :--- | :--- |
| `/nm help` | Barcha buyruqlar ro'yxatini ko'rsatish | `neomines.help` |
| `/nm gui (shaxta)` | Interaktiv boshqaruv GUI menyusini ochish | `neomines.gui` |
| `/nm list` | Barcha ro'yxatdan o'tgan shaxtalar ro'yxati | `neomines.list` |
| `/nm info <shaxta>` | Shaxta haqida to'liq texnik ma'lumotlar | `neomines.info` |
| `/nm create <shaxta>` | WorldEdit tanlovi bo'yicha yangi shaxta yaratish | `neomines.create` |
| `/nm delete <shaxta>` | Shaxtani butunlay o'chirish | `neomines.delete` |
| `/nm redefine <shaxta>` | Shaxta hududini yangi WorldEdit tanloviga ko'chirish | `neomines.redefine` |
| `/nm set <shaxta> [blok] (%)` | Shaxtaga yangi blok va uning foizini o'rnatish | `neomines.set` |
| `/nm unset <shaxta> (blok)` | Shaxtadan blokni olib tashlash yoki tozalash | `neomines.unset` |
| `/nm setdelay <shaxta> [vaqt]` | Reset taymerini (soniyalarda) belgilash | `neomines.setdelay` |
| `/nm resetmode <shaxta> [rejim]` | Reset rejimini belgilash (`TIME`/`PERCENTAGE`/`TIME_PERCENTAGE`) | `neomines.resetmode` |
| `/nm resetpercentage <shaxta> [%]` | Reset foizini belgilash | `neomines.resetpercentage` |
| `/nm reset <shaxta>` | Shaxtani zudlik bilan qo'lda yangilash | `neomines.reset` |
| `/nm flag <shaxta> <flag> [qiymat]` | Shaxta flaglarini sozlash | `neomines.flag` |
| `/nm start <shaxta>` | To'xtatilgan shaxtani ishga tushirish | `neomines.start` |
| `/nm stop <shaxta>` | Shaxtani to'xtatib turish | `neomines.stop` |
| `/nm starttasks` | Barcha shaxtalar taymerlarini boshlash | `neomines.starttasks` |
| `/nm stoptasks` | Barcha shaxtalar taymerlarini to'xtatish | `neomines.stoptasks` |
| `/nm tp <shaxta> (o'yinchi)` | Shaxtaga o'zini yoki boshqa o'yinchini teleport qilish | `neomines.teleport` |
| `/nm settp <shaxta>` | Shaxtaga teleport nuqtasini o'rnatish | `neomines.setteleport` |
| `/nm setresettp <shaxta>` | Reset paytida o'yinchilar otiladigan nuqtani belgilash | `neomines.setresetteleport` |
| `/nm sync` | Barcha shaxtalarni bir vaqtda sinxron yangilash | `neomines.sync` |
| `/nm reload [tur]` | Shaxtalar, config, xabarlar yoki parametrlarni qayta yuklash | `neomines.reload` |

---

## 🧩 PlaceholderAPI Kengaytmasi

- `%neomines_prefix%` yoki `%neomines_p%` — Plugin prefiksi
- `%neomines_<shaxta>_countdown%` — Formatlangan qolgan vaqt (hh:mm:ss)
- `%neomines_<shaxta>_time%` — Qolgan vaqt
- `%neomines_<shaxta>_remainingseconds%` — Qolgan aniq soniyalar
- `%neomines_<shaxta>_totalblocks%` — Shaxtadagi jami bloklar soni
- `%neomines_<shaxta>_remainingblocks%` — Qolgan bloklar soni
- `%neomines_<shaxta>_minedblocks%` — Qazib olingan bloklar soni
- `%neomines_<shaxta>_remainingblocksper%` — Qolgan bloklar foizi
- `%neomines_<shaxta>_resetpercentage%` — Reset bo'lish mezon foizi
- `%neomines_<shaxta>_timestring%` — Vaqt matn ko'rinishida

---

## 🛠️ Loyihani Yig'ish (Build)

Ushbu plagin **Java 21 LTS** talab qiladi.

```bash
# Windows
gradlew.bat clean shadowJar

# Linux / macOS
./gradlew clean shadowJar
```

Tayyorlangan JAR fayl `build/libs/NeoMines-1.0.0.jar` manzilida hosil bo'ladi.
Uni serveringizning `plugins/` papkasiga joylashtirishingiz kifoya!
