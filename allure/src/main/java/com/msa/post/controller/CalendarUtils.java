package com.msa.post.controller;//package com.msa.post.controller;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CalendarUtils {
//
//    public static List<List<LocalDate>> generateCalendarData() {
//        List<List<LocalDate>> weeks = new ArrayList<>();
//        LocalDate today = LocalDate.now();
//        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
//        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
//
//        // Start from the first day of the week of the current month
//        LocalDate firstDayOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() - 1);
//
//        for (LocalDate date = firstDayOfCalendar; date.isBefore(lastDayOfMonth.plusDays(1)); date = date.plusDays(1)) {
//            if (weeks.isEmpty() || date.getDayOfWeek().getValue() == 1) {
//                weeks.add(new ArrayList<>());
//            }
//            weeks.get(weeks.size() - 1).add(date);
//        }
//
//        return weeks;
//    }
//}