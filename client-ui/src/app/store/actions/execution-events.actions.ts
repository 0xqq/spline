/*
 * Copyright 2019 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Action } from '@ngrx/store';
import { PageableExecutionEvent } from 'src/app/generated/models';
import { Params } from '@angular/router';

export enum ExecutionEventsActionTypes {
    EXECUTION_EVENTS_GET = '[Execution Events] Get',
    EXECUTION_EVENTS_GET_SUCCESS = '[Execution Events] Get Success',
    EXECUTION_EVENTS_GET_DEFAULT = '[Execution Events] Get Default ',
    EXECUTION_EVENTS_GET_SUCESS_DEFAULT = '[Execution Events] Get Success Default',
}

export class Get implements Action {
    public readonly type = ExecutionEventsActionTypes.EXECUTION_EVENTS_GET
    constructor(public payload: Params) { }
}

export class GetDefault implements Action {
    public readonly type = ExecutionEventsActionTypes.EXECUTION_EVENTS_GET_DEFAULT
    constructor(public payload: Params) { }
}

export class GetSuccessDefault implements Action {
    public readonly type = ExecutionEventsActionTypes.EXECUTION_EVENTS_GET_SUCESS_DEFAULT
    constructor(public payload: Params) { }
}

export class GetSuccess implements Action {
    public readonly type = ExecutionEventsActionTypes.EXECUTION_EVENTS_GET_SUCCESS
    constructor(public payload: PageableExecutionEvent) { }
}

export type ExecutionEventsActions
    = Get
    | GetDefault
    | GetSuccess
    | GetSuccessDefault
