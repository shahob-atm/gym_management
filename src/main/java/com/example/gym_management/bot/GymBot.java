package com.example.gym_management.bot;

import com.example.gym_management.entity.Rate;
import com.example.gym_management.entity.User;
import com.example.gym_management.entity.UserRate;
import com.example.gym_management.projection.DailySubscriptionCount;
import com.example.gym_management.projection.GymUsersProjection;
import com.example.gym_management.projection.MonthsProfitOverview;
import com.example.gym_management.projection.UsersVisitedGymToday;
import com.example.gym_management.repository.GymRepo;
import com.example.gym_management.repository.RateRepo;
import com.example.gym_management.repository.UserRateRepo;
import com.example.gym_management.repository.UserRepo;
import com.example.gym_management.service.admin.AdminService;
import com.example.gym_management.wrapper.GymReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GymBot extends TelegramLongPollingBot {
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final UserRateRepo userRateRepo;
    private final AdminService adminService;
    private final GymRepo gymRepo;
    private final RateRepo rateRepo;
    private final Map<Long, String> userStates = new HashMap<>();

    Optional<User> selectUser = Optional.empty();

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return "Gym_Soft_bot";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String userState = userStates.getOrDefault(chatId, "START");

            if (userState.equals("START")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Salom botga xush kelibsiz! Iltimos, o'z telefon raqamingizni yuboring.");
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(getContactKeyboard());
                execute(sendMessage);

                userStates.put(chatId, "WAITING_FOR_CONTACT");
            } else if (userState.equals("WAITING_FOR_CONTACT")) {
                if (message.hasContact()) {
                    String phoneNumber = message.getContact().getPhoneNumber();

                    boolean exists = checkPhoneNumberInDatabase(phoneNumber);
                    System.out.println(exists);

                    if (exists) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("Telefon raqamingiz tasdiqlandi. Iltimos, parolni kiriting.");
                        sendMessage.setChatId(chatId);
                        execute(sendMessage);

                        userStates.put(chatId, "WAITING_FOR_PASSWORD");
                    } else {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("Kechirasiz, bunday raqam bizda mavjud emas. Qayta urinib ko'ring yoki ro'yxatdan o'ting: http://localhost:5173/");
                        sendMessage.setChatId(chatId);
                        execute(sendMessage);
                    }
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Siz ro'yxatdan o'tmagansiz ro'yxatdan o'ting: http://localhost:5173/");
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                }
            } else if (userState.equals("WAITING_FOR_PASSWORD")) {
                String password = message.getText();
                if (validatePassword(password)) {
                    StringBuilder messageBuilder = new StringBuilder();
                    String rollName = userRepo.roleName(selectUser.get().getUsername());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.enableMarkdown(true);

                    if (rollName != null) {

                        if (rollName.equals("ROLE_ADMIN")) {
                            messageBuilder.append("🎉 *Registratsiya muvaffaqiyatli yakunlandi!* 🎉\n\n");
                            messageBuilder.append("👑 *Admin*: ").append(selectUser.get().getFullName()).append("\n");
                            messageBuilder.append("📞 *Telefon*: ").append(selectUser.get().getUsername()).append("\n\n");
                            messageBuilder.append("🤝 *Botga xush kelibsiz!*");
                            sendMessage.setText(messageBuilder.toString());
                            sendMessage.setReplyMarkup(getAdminPanelInlineKeyboard());
                            sendMessage.setChatId(chatId);
                            userStates.put(chatId, "ADMIN");

                        } else if (rollName.equals("ROLE_USER")) {
                            messageBuilder.append("🎉 *Registratsiya muvaffaqiyatli yakunlandi!* 🎉\n\n");
                            messageBuilder.append("🙋‍♂️ *Foydalanuvchi*: ").append(selectUser.get().getFullName()).append("\n");
                            messageBuilder.append("📞 *Telefon*: ").append(selectUser.get().getUsername()).append("\n\n");
                            messageBuilder.append("🤝 *Botga xush kelibsiz!*");
                            userStates.put(chatId, "USER");
                        } else {
                            sendMessage.setText("xatolik yuz berdi");
                            sendMessage.setChatId(chatId);
                            userStates.put(chatId, "START");
                        }

                        execute(sendMessage);

                    } else {
                        sendMessage.setText("xatolik yuz berdi");
                        sendMessage.setChatId(chatId);
                        userStates.put(chatId, "WAITING");
                    }
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Noto'g'ri parol. Qayta urinib ko'ring.");
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                }
            } else if (userState.equals("WAITING")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("kuting siz bilan bog'lanamiz");
                sendMessage.setChatId(chatId);
                execute(sendMessage);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long calBackId = callbackQuery.getFrom().getId();
            String data = callbackQuery.getData();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(calBackId);

            if (data.equals("users")) {

                List<GymUsersProjection> allUserRate = getUsers(selectUser.get().getId(), "");

                StringBuilder messageBuilder = new StringBuilder();
                for (GymUsersProjection userRateProjection : allUserRate) {
                    messageBuilder.append("🙋‍♂️ *Foydalanuvchi*: ")
                            .append(userRateProjection.getFullName())
                            .append("\n")
                            .append("📋 *Tarif nomi*: ")
                            .append(userRateProjection.getName() == null ? "❌ Yo'q" : "✅ " + userRateProjection.getName())
                            .append("\n")
                            .append("📅 *Kunlar soni*: ")
                            .append(userRateProjection.getDay() == null ? "❌ Yo'q" : "🗓️ " + userRateProjection.getDay())
                            .append("\n\n");
                }

                sendMessage.setText(messageBuilder.toString());
                sendMessage.setReplyMarkup(getAdminPanelInlineKeyboard());
            } else if (data.equals("rate")) {
                StringBuilder messageBuilder = new StringBuilder();
                List<Rate> rates = getRates(selectUser.get().getId());

                if (!rates.isEmpty()) {
                    sendMessage.setParseMode("HTML");
                    messageBuilder.append("📋 <b><i>Sizning tariflaringiz:</i></b>\n\n");
                    rates.forEach(rate -> {
                        messageBuilder.append("💼 *Tarif nomi:* ").append(rate.getName()).append("\n");
                        messageBuilder.append("📅 *Kunlar soni:* ").append(rate.getDay()).append("\n");
                        messageBuilder.append("💳 *Narxi:* ").append(rate.getPrice()).append(" so'm\n");
                        messageBuilder.append("━━━━━━━━━━━━━━━━\n\n");
                    });
                } else {
                    messageBuilder.append("ℹ️ Sizda hozircha hech qanday tarif yo'q.");
                }

                sendMessage.setText(messageBuilder.toString());
                sendMessage.setReplyMarkup(getAdminPanelInlineKeyboard());

            } else if (data.equals("report")) {
                GymReportResponse report = getReport(selectUser.get().getId());
                StringBuilder messageBuilder = new StringBuilder();

                messageBuilder.append("📊 *Bugungi hisobot:*\n\n");
                messageBuilder.append("✔️ *Bugungi obunachilar soni:* ")
                        .append(report.getDailySubscriptionCount().size())
                        .append("\n");

                messageBuilder.append("🏋️ *Bugungi zalga tashrif buyurganlar soni:* ")
                        .append(report.getUsersVisitedGymToday().size())
                        .append("\n\n");

                messageBuilder.append("💰 *Oylik daromad:* \n");
                for (MonthsProfitOverview monthsProfitOverview : report.getMonthsProfitOverview()) {
                    messageBuilder.append("📅 *")
                            .append(monthsProfitOverview.getMonth())
                            .append(" oy:* ")
                            .append(monthsProfitOverview.getTotalRevenue())
                            .append(" so'm")
                            .append("\n");
                }

                sendMessage.setText(messageBuilder.toString());
                sendMessage.setReplyMarkup(getAdminPanelInlineKeyboard());

            }
            execute(sendMessage);
        }
    }

    private boolean validatePassword(String password) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(selectUser.get().getUsername(), password)
            );
            return authenticate.isAuthenticated();
        } catch (BadCredentialsException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean checkPhoneNumberInDatabase(String phoneNumber) {
        String normalizedNumber = phoneNumber.replaceAll("[^0-9]", "").substring(3);
        Optional<User> user = userRepo.findByUsername(normalizedNumber);
        selectUser = user;
        System.out.println(user);
        System.out.println(normalizedNumber);

        return user.isPresent();
    }

    private ReplyKeyboard getContactKeyboard() {
        KeyboardButton contactButton = new KeyboardButton("Kontaktni yuborish");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(Collections.singletonList(row));
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getAdminPanelInlineKeyboard() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Foydalanuvchilar");
        button1.setCallbackData("users");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Tarif");
        button2.setCallbackData("rate");

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Hisobot");
        button3.setCallbackData("report");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button1);
        row.add(button2);
        row.add(button3);

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setKeyboard(Collections.singletonList(row));

        return inlineKeyboard;
    }

    private List<GymUsersProjection> getUsers(UUID id, String keyword) {
        LocalDate today = LocalDate.now();
        List<GymUsersProjection> gymUsersProjections = gymRepo.getUsers(id, keyword);
        for (GymUsersProjection gymUsersProjection : gymUsersProjections) {
            String endTime = gymUsersProjection.getEndTime();
            if (endTime != null && LocalDate.parse(endTime).isBefore(today)) {

                String userRateId = gymUsersProjection.getUserRateId();
                UserRate userRate = userRateRepo.findById(UUID.fromString(userRateId)).orElseThrow();
                userRate.setActive(false);
                userRateRepo.save(userRate);
            }
        }
        List<GymUsersProjection> gymRepoUsersUpdate = gymRepo.getUsers(id, keyword);

        Map<UUID, List<GymUsersProjection>> groupedByUserId = gymRepoUsersUpdate.stream()
                .collect(Collectors.groupingBy(user -> UUID.fromString(user.getUsersId())));

        List<GymUsersProjection> filteredList = new ArrayList<>();

        for (Map.Entry<UUID, List<GymUsersProjection>> entry : groupedByUserId.entrySet()) {
            List<GymUsersProjection> userEntries = entry.getValue();

            List<GymUsersProjection> validEntries = userEntries.stream()
                    .filter(user -> (user.getDay() != null && user.getDay() > 0) || Boolean.TRUE.equals(user.getActive()))
                    .toList();

            if (!validEntries.isEmpty()) {
                Comparator<GymUsersProjection> comparator = Comparator.comparing((GymUsersProjection user) ->
                                Boolean.TRUE.equals(user.getActive()))
                        .thenComparing(user -> user.getDay() != null ? user.getDay() : Integer.MAX_VALUE);

                GymUsersProjection bestEntry = validEntries.stream()
                        .max(comparator)
                        .orElse(validEntries.get(0));
                filteredList.add(bestEntry);
            } else {
                filteredList.add(userEntries.get(0));
            }
        }

        return filteredList;
    }

    private GymReportResponse getReport(UUID id) {

        List<DailySubscriptionCount> dailySubscriptionCount = gymRepo.getDailySubscriptionCount(id);
        List<UsersVisitedGymToday> usersVisitedGymToday = gymRepo.getUsersVisitedGymToday(id);
        List<MonthsProfitOverview> monthsProfitOverview = gymRepo.getMonthsProfitOverview(id);

        return new GymReportResponse(dailySubscriptionCount, usersVisitedGymToday, monthsProfitOverview);
    }

    private List<Rate> getRates(UUID id) {
        return rateRepo.findByAdminId(id);
    }
}
