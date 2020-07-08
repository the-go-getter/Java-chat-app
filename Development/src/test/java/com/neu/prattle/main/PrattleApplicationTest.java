package com.neu.prattle.main;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrattleApplicationTest {
  @Test
  public void testPrattle(){
    try{
      PrattleApplication p = new PrattleApplication();
      p.getClasses();
    }catch (Exception a){
      fail();
    }
  }

}