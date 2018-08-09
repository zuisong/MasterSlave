/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingjdbc.core.api;

import io.shardingjdbc.core.hint.HintManagerHolder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The manager that use hint to inject sharding key directly through {@code ThreadLocal}.
 *
 * @author gaohongtao
 * @author zhangliang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HintManager implements AutoCloseable {


    @Getter
    private boolean masterRouteOnly;


    /**
     * Get a new instance for {@code HintManager}.
     *
     * @return {@code HintManager} instance
     */
    public static HintManager getInstance() {
        HintManager result = new HintManager();
        HintManagerHolder.setHintManager(result);
        return result;
    }


    /**
     * Set CRUD operation force route to master database only.
     */
    public void setMasterRouteOnly() {
        masterRouteOnly = true;
    }

    @Override
    public void close() {
        HintManagerHolder.clear();
    }
}
