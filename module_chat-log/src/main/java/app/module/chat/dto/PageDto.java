package app.module.chat.dto;

import java.util.List;

public class PageDto<T> {

  private List<T> items;
  private int page;
  private int size;
  private long total;

}

