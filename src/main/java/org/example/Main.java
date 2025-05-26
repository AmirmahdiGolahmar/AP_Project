package org.example;
import java.util.logging.LogManager;
import static spark.Spark.*;

import controller.RestaurantController;
import controller.UserController;
import entity.Customer;
import entity.User;
import entity.BankInfo;
import com.google.gson.Gson;
import java.util.List;

import static spark.Spark.*;
import com.google.gson.Gson;
import service.*;

import java.util.List;
import java.util.logging.LogManager;

import static spark.Spark.*;

import com.google.gson.Gson;
import entity.*;
import service.*;

import java.util.List;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset();

        port(4567);
        UserController.initRoutes();
        RestaurantController.initRoutes();
    }
}

/*

module DFF (
        input clk,
        input reset,
        input d,
        output reg q
        );
        always @(posedge clk or posedge reset)
        if (reset)
        q <= 1'b0;
        else
        q <= d;
        endmodule

        module Register4bit (
        input clk,
        input enable,
        input [3:0] din,
        output reg [3:0] dout
        );
        always @(posedge clk) begin
        if (enable)
        dout <= din;
        end
        endmodule

        module SecureFSM_Moore (
        input clk,
        input reset,
        input request,
        input confirm,
        input [3:0] data_in,
        output reg load_reg1,
        output reg load_reg2,
        output reg [2:0] state
        );

        localparam IDLE         = 3'b000,
        ACTIVE       = 3'b001,
        WAIT_PASS    = 3'b010,
        CHECK_PASS   = 3'b011,
        REQUEST_DATA = 3'b101,
        SAVE_ODD     = 3'b110,
        SAVE_EVEN    = 3'b100,
        TRAP         = 3'b111;

        localparam [3:0] CORRECT_PASS = 4'b1010;

        always @(posedge clk or posedge reset) begin
        if (reset)
        state <= IDLE;
        else begin
        case (state)
        IDLE:
        state <= request ? ACTIVE : IDLE;

        ACTIVE:
        state <= WAIT_PASS;

        WAIT_PASS:
        if (!request)
        state <= IDLE;
        else if (confirm)
        state <= CHECK_PASS;

        CHECK_PASS:
        if (!request)
        state <= IDLE;
        else if (data_in == CORRECT_PASS)
        state <= REQUEST_DATA;
        else
        state <= TRAP;

        REQUEST_DATA:
        if (!request)
        state <= IDLE;
        else if (confirm)
        state <= data_in[0] ? SAVE_ODD : SAVE_EVEN;

        SAVE_ODD, SAVE_EVEN:
        state <= request ? IDLE : IDLE;

        TRAP:
        if (!request)
        state <= IDLE;

        default:
        state <= IDLE;
        endcase
        end
        end

        // خروجی‌ها فقط تابع state هستند → Moore واقعی
        always @(*) begin
        load_reg1 = 0;
        load_reg2 = 0;
        case (state)
        SAVE_ODD:  load_reg1 = 1;
        SAVE_EVEN: load_reg2 = 1;
        endcase
        end

        endmodule


        module SecureSystem (
        input clk,
        input reset,
        input request,
        input confirm,
        input [3:0] data_in,
        output [3:0] reg1_out,
        output [3:0] reg2_out,
        output [2:0] fsm_state
        );

        wire load1, load2;
        wire [2:0] state;

        // FSM
        SecureFSM_Moore fsm (
        .clk(clk),
        .reset(reset),
        .request(request),
        .confirm(confirm),
        .data_in(data_in),
        .load_reg1(load1),
        .load_reg2(load2),
        .state(state)
        );

        assign fsm_state = state;

        // Register for odd data
        Register4bit reg1 (
        .clk(clk),
        .enable(load1),
        .din(data_in),
        .dout(reg1_out)
        );

        // Register for even data
        Register4bit reg2 (
        .clk(clk),
        .enable(load2),
        .din(data_in),
        .dout(reg2_out)
        );

        endmodule

 */

