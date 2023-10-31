package com.example.redisstudy.BloomFilterDemo;

import java.util.HashSet;

public class HashCodeConflictDemo {
    public static void main(String[] args) {
        HashSet<Integer> sets = new HashSet<>();
        for (int i = 0; i < 200000; i++) {
            int hashCode = new Object().hashCode();
            if (sets.contains(hashCode)){
                System.out.println("出现了重复的hashcode: "+hashCode+"\t 运行到"+i);
                continue;
            }
            sets.add(hashCode);
        }
        System.out.println("Aa".hashCode());
        System.out.println("BB".hashCode());
        System.out.println("柳柴".hashCode());
        System.out.println("柴柕".hashCode());
    }
}
