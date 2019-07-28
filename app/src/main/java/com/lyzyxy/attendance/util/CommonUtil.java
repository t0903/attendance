package com.lyzyxy.attendance.util;

import com.lyzyxy.attendance.model.dto.RecordDto;

import java.util.List;

public class CommonUtil {
    /**
     * 根据学号，和班级信息找出排名
     * @param list
     * @param id
     */
    public static int[] Rank(List<RecordDto> list,int id){
        int r = 1;
        int rr = 0;
        double s = list.get(0).getRate();
        for (RecordDto dto : list){
            if(s != dto.getRate())
                r++;

            if(dto.getId() == id){
                rr = (int)(dto.getRate()*100);
                break;
            }else{
                s = dto.getRate();
            }
        }

        return new int[]{r,rr};
    }
}
