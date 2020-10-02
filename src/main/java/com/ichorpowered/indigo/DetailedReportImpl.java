/*
 * This file is part of Indigo, licensed under the MIT License (MIT).
 *
 * Copyright (c) IchorPowered <http://ichorpowered.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ichorpowered.indigo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/* package */ final class DetailedReportImpl implements DetailedReport {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final Map<String, DetailedReportCategoryImpl> categories = new LinkedHashMap<>();
  private final String message;
  private final @Nullable Throwable throwable;

  /* package */ DetailedReportImpl(final @NonNull String message, final @Nullable Throwable throwable) {
    this.message = message;
    this.throwable = throwable;
  }

  @Override
  public @NonNull String message() {
    return this.message;
  }

  @Override
  public @Nullable Throwable throwable() {
    return this.throwable;
  }

  @Override
  public @NonNull DetailedReportCategory category(final @NonNull String name) {
    return this.categories.computeIfAbsent(name, key -> new DetailedReportCategoryImpl(name));
  }

  @Override
  public void raise() {
    throw new DetailedReportedException(this);
  }

  @Override
  public @NonNull JsonObject toJson() {
    final JsonObject object = new JsonObject();
    object.addProperty("instant", Instant.now().toString());
    object.addProperty("message", this.message);
    if(this.throwable != null) {
      object.add("throwable", throwable(this.throwable));
    }
    final JsonObject details = new JsonObject();
    this.appendCategories(details);
    object.add("details", details);
    return object;
  }

  @Override
  public @NonNull String toString() {
    final JsonObject object = this.toJson();
    return GSON.toJson(object);
  }

  private static JsonObject throwable(final Throwable throwable) {
    final JsonObject object = new JsonObject();
    object.addProperty("type", throwable.getClass().getName());
    object.addProperty("message", throwable.getMessage());
    final JsonArray stack = new JsonArray();
    for(final StackTraceElement element : throwable.getStackTrace()) {
      stack.add(element.toString());
    }
    object.add("stack", stack);
    final Throwable cause = throwable.getCause();
    if(cause != null) {
      object.add("cause", throwable(cause));
    }
    return object;
  }

  private void appendCategories(final JsonObject sb) {
    for(final DetailedReportCategoryImpl category : this.categories.values()) {
      sb.add(category.name, category.write());
    }
  }

  private final class DetailedReportCategoryImpl implements DetailedReportCategory {
    private final String name;
    private final List<Entry> entries = new ArrayList<>();

    /* package */ DetailedReportCategoryImpl(final String name) {
      this.name = name;
    }

    @Override
    public @NonNull DetailedReportCategory detail(final @NonNull String key, final @Nullable Object value) {
      this.entries.add(new NormalEntry(key, value));
      return this;
    }

    @Override
    public @NonNull DetailedReportCategory complexDetail(final @NonNull String key, final @NonNull Consumer<DetailedReportCategory> consumer) {
      this.entries.add(new ConsumerEntry(key, consumer));
      return this;
    }

    @Override
    public @NonNull DetailedReport then() {
      return DetailedReportImpl.this;
    }

    @SuppressWarnings("unchecked")
    JsonObject write() {
      final JsonObject object = new JsonObject();
      for(final Entry entry : this.entries) {
        if(entry instanceof ConsumerEntry) {
          final DetailedReportCategoryImpl category = new DetailedReportCategoryImpl(entry.key);
          ((Consumer<DetailedReportCategory>) entry.value()).accept(category);
          object.add(entry.key, category.write());
        } else {
          final Object value = entry.value();
          if(value instanceof JsonElement) {
            object.add(entry.key, (JsonElement) value);
          } else {
            object.addProperty(entry.key, String.valueOf(value));
          }
        }
      }
      return object;
    }

    private abstract class Entry {
      private final String key;

      /* package */ Entry(final String key) {
        this.key = key;
      }

      abstract Object value();
    }

    private class NormalEntry extends Entry {
      private final Object value;

      /* package */ NormalEntry(final String key, final @Nullable Object value) {
        super(key);

        // Get the string representation of the value now, as the object may change when actually outputting
        if(value == null) {
          this.value = "~~NULL~~";
        } else if(value instanceof Throwable) {
          this.value = throwable((Throwable) value);
        } else {
          this.value = value.toString();
        }
      }

      @Override
      Object value() {
        return this.value;
      }
    }

    private final class ConsumerEntry extends Entry {
      private final Consumer<DetailedReportCategory> consumer;

      /* package */ ConsumerEntry(final String key, final @NonNull Consumer<DetailedReportCategory> consumer) {
        super(key);
        this.consumer = consumer;
      }

      @Override
      Object value() {
        return this.consumer;
      }
    }
  }
}
