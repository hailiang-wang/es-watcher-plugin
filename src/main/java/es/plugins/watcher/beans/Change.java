/*
   Copyright 2012 Thomas Peuss
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package es.plugins.watcher.beans;

public class Change implements Comparable<Change> {
    private long timestamp;
    private Type type;
    private String id;
    private long version;
    private String payload;
    private String index;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public enum Type {
        INDEX, CREATE, DELETE;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return this.index;
    }

    @Override
    public int compareTo(Change o) {
        if (this.timestamp < o.timestamp) {
            return -1;
        } else if (this.timestamp > o.timestamp) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Change)) {
            return false;
        }

        Change other = (Change) o;
        return this.timestamp == other.timestamp;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // GSON library for JSON
    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}