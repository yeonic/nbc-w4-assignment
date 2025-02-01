package me.yeon.week4.global.common;

import lombok.Getter;

@Getter
public class Paging {

  private int pageSize;
  private int pageNum;

  public Paging(int pageSize, int pageNum) {
    this.pageSize = Math.max(pageSize, 1);
    this.pageNum = Math.max(pageNum, 1);
  }

  public int getOffset() {
    return (pageNum - 1) * pageSize;
  }
}
