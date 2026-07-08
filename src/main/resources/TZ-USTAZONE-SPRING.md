*************** Requirements ***************
1. GIT, Setting (package, properties, db)
2. log
3. security + jwt (role: CLIENT, MASTER, ADMIN)
4. Swagger
5. flyway (admin, category, order, subscription, notification ... barcha jadval)
6. Password encoding — BCrypt
7. internationalization (uz, ru, en)
8. http

***
9. Trigger — order.status = 'confirmed' bo'lganda avtomatik commission/master_amount hisoblash va transactions yozuvlarini yaratish (payment, commission, payout).
10. Trigger — review INSERT bo'lganda master_profiles.rating qayta hisoblanadi (avg).
11. Trigger — order.status = 'confirmed' bo'lganda profiles.bonus_balance += amount * 1% (cashback).
12. Scheduled job (order-timeout) — status='pending' bo'lgan order 15 daqiqada accept bo'lmasa avtomatik cancelled (cancel_reason='no_master').
13. Scheduled job (subscription-expire) — kunlik ishlaydi, trial_ends_at yoki expires_at o'tgan subscription'larni tier='free' qiladi.
14. Notification yuborish (WebSocket/STOMP + SMS orqali Eskiz.uz + kelajakda Firebase Push).
15. Function — bitta so'rovda like_count/dislike_count singari, master uchun rating+jobs_completed+active_orders_count ni birga qaytaruvchi metod.
16. AI Match — tashqi AI provider (masalan OpenAI/Lovable AI Gateway) orqali order tavsifiga mos top-10 ustani score bo'yicha topish.

*************** UstaZone ***************

1. Profile (auth.users o'rnini bosuvchi asosiy foydalanuvchi jadvali)
    id(uuid), full_name, email, phone(sensitiv), password(hash), avatar_attach_id,
    role(CLIENT,MASTER,ADMIN), city, region, latitude, longitude,
    is_verified, is_blocked, bonus_balance, status(ACTIVE,BLOCKED), created_date

2. Attach
    id(uuid), origin_name, size, type(extension), path, created_date

3. Category (2 darajali daraxt)
    id(uuid), name_uz, name_ru, name_en, icon, color, order_num,
    parent_id → category(id) ON DELETE CASCADE, slug, created_date

4. MasterProfile
    id(uuid), profile_id(unique) → profile(id), bio, experience_years, rating,
    jobs_completed, is_verified, verification_tier(BASIC,STANDARD,PREMIUM),
    balance, withdrawable_balance, category_ids(uuid[]), service_radius_km,
    work_days(varchar[]), work_start(time), work_end(time), accepts_emergency(bool),
    id_document_attach_id(sensitiv), selfie_attach_id(sensitiv),
    certificate_attach_ids(sensitiv), card_number(sensitiv), bank_account(sensitiv),
    tax_info(sensitiv), created_date

5. Service
    id(uuid), master_id → master_profile(id), subcategory_id → category(id),
    price_type(FIXED,FROM,HOURLY), price(numeric), is_negotiable(bool),
    experience_years, portfolio_attach_ids(uuid[]), status(ACTIVE,INACTIVE), created_date

6. Order
    id(uuid), client_id → profile(id), master_id(nullable) → profile(id),
    category_id → category(id), title, description, city, address,
    payment_method(CASH,CLICK,PAYME,BONUS) default CASH,
    status(PENDING,ACCEPTED,ENROUTE,IN_PROGRESS,COMPLETED,CONFIRMED,PAID,REVIEWED,CANCELLED,DISPUTED)
      default PENDING,
    amount(numeric), commission_amount(numeric), master_amount(numeric),
    client_confirmed(bool), master_confirmed(bool), admin_approved(bool), is_dispute(bool),
    is_emergency(bool) default false, client_lat, client_lng, master_lat, master_lng,
    promo_code_id → promo_code(id), discount(numeric), bonus_used(numeric),
    cancel_reason(varchar),
    accepted_date, enroute_date, started_date, completed_date, cancelled_date,
    created_date, updated_date

7. Message (chat)
    id(uuid), sender_id → profile(id), receiver_id → profile(id),
    order_id(nullable) → order(id), content, attachment_id(nullable) → attach(id),
    is_read(bool) default false, created_date

8. Notification
    id(uuid), user_id → profile(id), sender_id(nullable) → profile(id),
    title, message, type(INFO,ORDER_NEW,ORDER_ACCEPTED,EMERGENCY_ORDER,
      VERIFICATION_APPROVED,VERIFICATION_REJECTED,BROADCAST,PAYMENT,REVIEW),
    related_order_id(nullable) → order(id), is_read(bool) default false,
    claimed_by(nullable) → profile(id), claimed_date, created_date

9. Transaction
    id(uuid), user_id → profile(id), order_id(nullable) → order(id),
    type(PAYMENT,COMMISSION,PAYOUT,REFUND,BONUS,CASHBACK), amount(numeric),
    status(PENDING,SUCCESS,FAILED), provider(CLICK,PAYME,CASH,INTERNAL),
    provider_ref(varchar), created_date

10. SubscriptionPlan
    id(uuid), tier(FREE,BASIC,STANDARD,PRO,PREMIUM,VIP), audience(CLIENT,MASTER),
    price(numeric), duration_months, max_services, ai_match_boost, badge,
    is_active(bool), created_date

11. Subscription
    id(uuid), user_id(unique) → profile(id), tier, is_trial(bool),
    trial_ends_date, expires_date, billing_period(MONTHLY,YEARLY), audience,
    created_date, updated_date

12. Review
    id(uuid), order_id(unique per client) → order(id), client_id → profile(id),
    master_id → profile(id), rating(1..5), comment, created_date

13. FavoriteMaster
    id(uuid), client_id → profile(id), master_id → profile(id), created_date

14. SavedAddress
    id(uuid), profile_id → profile(id), title, city, address, latitude, longitude, created_date

15. Complaint
    id(uuid), profile_id → profile(id), content, entity_id(uuid), type(ORDER,MASTER,CLIENT),
    status(OPEN,RESOLVED,REJECTED), created_date

16. PromoCode
    id(uuid), code(unique), discount_percent, discount_amount, max_usage, used_count,
    valid_from, valid_to, is_active(bool), created_date

17. PromoRedemption
    id(uuid), promo_code_id → promo_code(id), profile_id → profile(id),
    order_id → order(id), created_date

18. WithdrawRequest
    id(uuid), master_id → profile(id), amount(numeric),
    status(PENDING,APPROVED,REJECTED,PAID), provider(CLICK,PAYME,BANK),
    provider_ref, created_date, processed_date

19. MasterAvailability
    id(uuid), master_id → profile(id), day_of_week, start_time, end_time, is_available(bool)

20. PlatformSettings
    id(uuid), commission_percent(numeric), key(varchar), value(varchar), updated_date

21. Broadcast
    id(uuid), admin_id → profile(id), audience(ALL,CLIENTS,MASTERS), title, message,
    send_sms(bool), created_date

22. EmailHistory / SmsHistory
    id(uuid), to_contact, title, message, status(SENT,FAILED), created_date


*** Task List ***

0. Auth
    1. POST /api/v1/auth/register (with email verification)
        Request: full_name, email, password, role(CLIENT,MASTER)
    2. POST /api/v1/auth/login
        Request: email, password → Response: accessToken, refreshToken
    3. POST /api/v1/auth/refresh-token
    4. POST /api/v1/auth/verify-email
    5. POST /api/v1/auth/forgot-password
    6. POST /api/v1/auth/reset-password

1. Profile
    1. PATCH /api/v1/profile/password (CLIENT,MASTER,ADMIN)
        Change password
    2. PATCH /api/v1/profile/email (with email verification)
    3. PATCH /api/v1/profile/detail
        (full_name, city, region, latitude, longitude)
    4. PATCH /api/v1/profile/avatar
        (upload, delete old attach)
    5. GET /api/v1/profile/me
        Response: id, full_name, email, phone, avatar(id,url), role, city, region
    6. POST /api/v1/profile/admin-create (ADMIN)
        (full_name, email, role(ADMIN))
    7. GET /api/v1/profile/{id} (ADMIN)
    8. GET /api/v1/profile/pagination (ADMIN)
        filter: role, city, is_blocked
    9. PATCH /api/v1/profile/{id}/block (ADMIN)
    10. GET /api/v1/profile/{id}/sensitive (ADMIN) — phone, card_number va h.k. faqat shu orqali

2. Attach
    1. POST /api/v1/attach (upload)
    2. GET /api/v1/attach/open/{id} (open)
    3. GET /api/v1/attach/download/{id}
    4. GET /api/v1/attach/pagination (ADMIN)
        Response: id, origin_name, size, url
    5. DELETE /api/v1/attach/{id} (delete from db and system) (ADMIN)

3. Category
    1. POST /api/v1/category (ADMIN)
    2. PUT /api/v1/category/{id} (ADMIN)
    3. DELETE /api/v1/category/{id} (ADMIN)
    4. GET /api/v1/category/list
        (2 darajali: parent + children)
    5. GET /api/v1/category/{id}/subcategories

4. MasterProfile
    1. POST /api/v1/master-profile/onboarding (MASTER)
        (bio, experience_years, category_ids, service_radius_km, work_days, work_start/end)
    2. PATCH /api/v1/master-profile/detail (MASTER, OWNER)
    3. POST /api/v1/master-profile/verification-documents (MASTER, OWNER)
        (id_document, selfie, certificates upload)
    4. PATCH /api/v1/master-profile/{id}/verify (ADMIN)
        (verification_tier, is_verified)
    5. GET /api/v1/master-profile/{id}
        MasterProfileFullInfo (public qism + sensitivsiz)
    6. GET /api/v1/master-profile/pagination (ADMIN)
        filter: category_id, city, is_verified, verification_tier
    7. GET /api/v1/master-profile/me/balance (MASTER, OWNER)
    8. GET /api/v1/master-profile/{id}/stats
        Function: rating + jobs_completed + active_orders_count bitta so'rovda

5. Service
    1. POST /api/v1/service (MASTER)
        Limit: free tier — 3 tagacha
    2. PUT /api/v1/service/{id} (MASTER, OWNER)
    3. DELETE /api/v1/service/{id} (MASTER, OWNER)
    4. PATCH /api/v1/service/{id}/status (MASTER, OWNER)
    5. GET /api/v1/service/by-master/{masterId}
    6. GET /api/v1/service/pagination (ADMIN)

------------------------------------------------------------------------------
6. Order — **eng muhim modul**
    1. POST /api/v1/orders (CLIENT)
        (category_id, title, description, city, address, payment_method, is_emergency)
        → status='PENDING', keyin ai-match chaqiriladi
    2. GET /api/v1/orders/{id}
        OrderFullInfo (agar DISPUTED bo'lsa yoki private — faqat OWNER/MASTER/ADMIN)
    3. GET /api/v1/orders/pagination-by-client (CLIENT, OWNER)
        OrderShortInfo
    4. GET /api/v1/orders/pagination-by-master (MASTER, OWNER)
        filter: status
    5. GET /api/v1/orders/available-for-master (MASTER)
        (status='PENDING', category mos, service_radius ichida)
    6. GET /api/v1/orders/pagination (ADMIN)
        filter: status, city, category_id, dateFrom, dateTo
    7. POST /api/v1/orders/{id}/claim (MASTER)
        Row-level lock (SELECT ... FOR UPDATE) — birinchi accept qilgan oladi
        → status='ACCEPTED', master_id to'ladi, accepted_date=now()
        → boshqa yuborilgan notification'lar claimed_by/claimed_date bilan yopiladi
    8. PATCH /api/v1/orders/{id}/advance
        (to: ENROUTE | IN_PROGRESS | COMPLETED | CONFIRMED | CANCELLED | DISPUTED)
        Har bir o'tish uchun ruxsat va state-machine tekshiruvi (§ pastda)
    9. PATCH /api/v1/orders/{id}/location (MASTER, OWNER)
        (master_lat, master_lng) — LiveTracker uchun, WebSocket orqali ham broadcast
    10. POST /api/v1/orders/{id}/dispute (CLIENT yoki MASTER)
        (reason) → is_dispute=true, status='DISPUTED'
    11. PATCH /api/v1/orders/{id}/resolve-dispute (ADMIN)
        (resolution: CONFIRMED | CANCELLED, admin_approved=true)
    12. GET /api/v1/orders/{id}/history
        (status o'zgarish tarixi, agar alohida order_status_history jadvali yuritilsa)

    **Order Status Flow:**
    ```
    PENDING → ACCEPTED | CANCELLED
    ACCEPTED → ENROUTE | CANCELLED
    ENROUTE → IN_PROGRESS | CANCELLED
    IN_PROGRESS → COMPLETED | DISPUTED
    COMPLETED → CONFIRMED | DISPUTED
    CONFIRMED → PAID (avtomatik trigger: commission + master_amount hisoblanadi)
    PAID → REVIEWED
    * → DISPUTED
    DISPUTED → CONFIRMED | CANCELLED (faqat ADMIN, admin_approved=true)
    ```
    - CLIENT qila oladi: PENDING→CANCELLED, COMPLETED→CONFIRMED, *→DISPUTED
    - MASTER qila oladi: ACCEPTED→ENROUTE→IN_PROGRESS→COMPLETED, *→DISPUTED
    - ADMIN: istalgan o'tish

    **Timeout job'lar:**
    - PENDING 15 daqiqada accept bo'lmasa → CANCELLED (cancel_reason='no_master')
    - ENROUTE 60 daqiqada IN_PROGRESS bo'lmasa → CLIENT'ga eslatma notification
    - COMPLETED 48 soatda CONFIRMED bo'lmasa → avtomatik CONFIRMED

    OrderFullInfo(id, title, description, city, address, category(id,name),
        client(id,full_name,phone,avatar_url), master(id,full_name,phone,avatar_url,rating),
        status, payment_method, amount, commission_amount, master_amount,
        client_confirmed, master_confirmed, is_dispute, is_emergency,
        client_lat, client_lng, master_lat, master_lng, created_date)
    OrderShortInfo(id, title, category(id,name), status, amount, master(id,full_name,avatar_url),
        created_date)

------------------------------------------------------------------------------
7. AI Match
    1. POST /api/v1/ai-match/{orderId} (ichki, order yaratilganda avtomatik chaqiriladi)
        Response: top 10 master_id + score ro'yxati
    2. GET /api/v1/ai-match/{orderId}/candidates (ADMIN, debugging uchun)

    **Score formulasi (0..200):**
    ```
    score = rating * 10                          -- 0..50
          + LEAST(jobs_completed, 100) * 0.3     -- 0..30
          + tier_boost                            -- free 0, pro +15, premium +30
          + distance_score                        -- 0..50 (Haversine, yaqinroq yuqoriroq)
          + verification_bonus                    -- verified +10, premium tier +15
          + response_time_bonus                   -- oxirgi 30 kun o'rtacha accept vaqti: <5min +10, <15min +5
          + price_fit_bonus                        -- narx client budjetiga mos bo'lsa +10
          - blocked_penalty                        -- is_blocked bo'lsa -1000
    ```
    service_radius_km dan tashqarida bo'lsa score=0 va ro'yxatga kirmaydi.
    Emergency order — top-1 avtomatik birinchi bo'lib ko'radi, qolgan 9 tasi navbatda.

    3. POST /api/v1/ai-estimate (CLIENT)
        (description) → AI orqali narx oralig'ini taxminlash
        Response: min_price, max_price, suggested_category_id

------------------------------------------------------------------------------
8. Message (Chat)
    1. POST /api/v1/messages (CLIENT,MASTER)
        (receiver_id, order_id, content, attachment_id)
    2. GET /api/v1/messages/by-order/{orderId}
        pagination, created_date asc
    3. GET /api/v1/messages/conversations (murojat qilgan user)
        (oxirgi xabar + unread count har bir suhbatdosh bo'yicha)
    4. PATCH /api/v1/messages/{id}/read
    5. WebSocket: /ws/chat — real-time xabar yetkazish (STOMP)

------------------------------------------------------------------------------
9. Notification
    1. GET /api/v1/notifications/pagination (murojat qilgan user)
        filter: type, is_read, dateFrom, dateTo
    2. PATCH /api/v1/notifications/{id}/read
    3. PATCH /api/v1/notifications/read-all
    4. POST /api/v1/notifications/admin-send (ADMIN)
        (profile_id yoki broadcast: audience, title, message, send_sms)
    5. WebSocket: /ws/notifications — real-time bell
    6. Kanal: DB (bell, real-time) + SMS (Eskiz.uz, muhim voqealarda) + Firebase Push (kelajakda)

------------------------------------------------------------------------------
10. Transaction / Payment
    1. POST /api/v1/payments/process (CLIENT)
        (order_id, provider: CLICK,PAYME,CASH,BONUS)
    2. POST /api/v1/payments/callback/click (verify_jwt=false, provider webhook)
    3. POST /api/v1/payments/callback/payme (verify_jwt=false, provider webhook)
    4. GET /api/v1/transactions/pagination (ADMIN)
        filter: user_id, order_id, type, status, dateFrom, dateTo
    5. GET /api/v1/transactions/me (murojat qilgan user)

    **Commission hisoblash (order CONFIRMED bo'lganda trigger):**
    ```
    commission_amount = round(amount * commission_percent / 100)
    master_amount = amount - commission_amount
    ```
    Transactions'ga 3 ta yozuv: PAYMENT (client -amount), COMMISSION (platform +commission),
    PAYOUT (master +master_amount).

11. WithdrawRequest
    1. POST /api/v1/withdraw-requests (MASTER)
        (amount) — withdrawable_balance dan yechiladi
    2. GET /api/v1/withdraw-requests/pagination (ADMIN)
        filter: status
    3. PATCH /api/v1/withdraw-requests/{id}/approve (ADMIN)
    4. PATCH /api/v1/withdraw-requests/{id}/reject (ADMIN)

------------------------------------------------------------------------------
12. Subscription
    1. GET /api/v1/subscription-plans/list
        filter: audience(CLIENT,MASTER)
    2. POST /api/v1/subscription-plans (ADMIN)
    3. PUT /api/v1/subscription-plans/{id} (ADMIN)
    4. GET /api/v1/subscriptions/me
        Response: tier, is_trial, trial_ends_date, expires_date
    5. POST /api/v1/subscriptions/activate (CLIENT,MASTER — to'lovdan keyin)
        (tier, months)
    6. GET /api/v1/subscriptions/pagination (ADMIN)
        filter: tier, is_trial

    **Trial:**
    - MASTER: ro'yxatdan o'tganda avtomatik 30 kun PRO trial
    - CLIENT: ro'yxatdan o'tganda avtomatik 7 kun PRO trial
    - Tugagach (scheduled job, kunlik): tier='FREE', expires_date=now()
    - FREE tier MASTER order accept qila olmaydi (master_can_accept_orders=false)

------------------------------------------------------------------------------
13. Review
    1. POST /api/v1/reviews (CLIENT)
        (order_id, rating 1..5, comment) — faqat o'z tugagan (PAID) orderi uchun, 1 marta
    2. GET /api/v1/reviews/by-master/{masterId}
        pagination, created_date desc
    3. DELETE /api/v1/reviews/{id} (ADMIN)
        Trigger: master_profiles.rating qayta hisoblanadi

14. FavoriteMaster
    1. POST /api/v1/favorite-masters (CLIENT)
        (master_id)
    2. DELETE /api/v1/favorite-masters/{masterId} (CLIENT, OWNER)
    3. GET /api/v1/favorite-masters/me

15. SavedAddress
    1. POST /api/v1/saved-addresses (CLIENT,MASTER)
    2. PUT /api/v1/saved-addresses/{id} (OWNER)
    3. DELETE /api/v1/saved-addresses/{id} (OWNER)
    4. GET /api/v1/saved-addresses/me

16. Complaint
    1. POST /api/v1/complaints (CLIENT,MASTER)
        (entity_id, type(ORDER,MASTER,CLIENT), content)
    2. GET /api/v1/complaints/pagination (ADMIN)
        filter: type, status
    3. PATCH /api/v1/complaints/{id}/resolve (ADMIN)
    4. GET /api/v1/complaints/by-profile/{profileId} (ADMIN)

17. PromoCode
    1. POST /api/v1/promo-codes (ADMIN)
    2. PUT /api/v1/promo-codes/{id} (ADMIN)
    3. DELETE /api/v1/promo-codes/{id} (ADMIN)
    4. POST /api/v1/promo-codes/apply (CLIENT)
        (code, order_id) → discount hisoblanadi
    5. GET /api/v1/promo-codes/pagination (ADMIN)

18. MasterAvailability
    1. PUT /api/v1/master-availability (MASTER, OWNER)
        (haftalik jadval: day_of_week, start_time, end_time, is_available)
    2. GET /api/v1/master-availability/{masterId}

19. PlatformSettings
    1. GET /api/v1/platform-settings (ADMIN)
    2. PATCH /api/v1/platform-settings (ADMIN)
        (commission_percent va boshqa key-value sozlamalar)

20. Broadcast
    1. POST /api/v1/broadcasts (ADMIN)
        (audience: ALL,CLIENTS,MASTERS; title, message, send_sms)
        → barcha mos foydalanuvchilarga notification + ixtiyoriy SMS

21. EmailHistory / SmsHistory
    1. GET /api/v1/email-history/pagination (ADMIN)
        filter: to_contact, dateFrom, dateTo
    2. GET /api/v1/sms-history/pagination (ADMIN)
        filter: to_contact, dateFrom, dateTo

------------------------------------------------------------------------------
22. Analytics / Dashboard (ADMIN)
    1. GET /api/v1/analytics/summary
        (jami order, jami tushum, aktiv master/client soni, o'rtacha rating)
    2. GET /api/v1/analytics/orders-by-status
    3. GET /api/v1/analytics/revenue-by-period
        filter: dateFrom, dateTo, groupBy(day,week,month)
