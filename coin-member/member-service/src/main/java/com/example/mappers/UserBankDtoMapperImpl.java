//package com.example.mappers;
//
//import com.example.domin.UserBank;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class UserBankDtoMapperImpl {
//    public UserBank toConvertEntity(UserBankDto source) {
//        if (source == null) {
//            return null;
//        } else {
//            UserBank userBank = new UserBank();
//            userBank.setRealName(source.getRealName());
//            userBank.setBank(source.getBank());
//            userBank.setBankProv(source.getBankProv());
//            userBank.setBankCity(source.getBankCity());
//            userBank.setBankAddr(source.getBankAddr());
//            userBank.setBankCard(source.getBankCard());
//            return userBank;
//        }
//    }
//
//    public UserBankDto toConvertDto(UserBank source) {
//        if (source == null) {
//            return null;
//        } else {
//            UserBankDto userBankDto = new UserBankDto();
//            userBankDto.setRealName(source.getRealName());
//            userBankDto.setBank(source.getBank());
//            userBankDto.setBankProv(source.getBankProv());
//            userBankDto.setBankCity(source.getBankCity());
//            userBankDto.setBankAddr(source.getBankAddr());
//            userBankDto.setBankCard(source.getBankCard());
//            return userBankDto;
//        }
//    }
//
//    public List<UserBank> toConvertEntity(List<UserBankDto> source) {
//        if (source == null) {
//            return null;
//        } else {
//            List<UserBank> list = new ArrayList(source.size());
//
//            for(UserBankDto userBankDto : source) {
//                list.add(this.toConvertEntity(userBankDto));
//            }
//
//            return list;
//        }
//    }
//
//    public List<UserBankDto> toConvertDto(List<UserBank> source) {
//        if (source == null) {
//            return null;
//        } else {
//            List<UserBankDto> list = new ArrayList(source.size());
//
//            for(UserBank userBank : source) {
//                list.add(this.toConvertDto(userBank));
//            }
//
//            return list;
//        }
//    }
//}
