package com.pyotrpopov443.fourier

class Complex(var real: Float, var imaginary: Float) {

    fun add(complex: Complex) {
        this.real += complex.real
        this.imaginary += complex.imaginary
    }

    fun multiply(complex: Complex): Complex {
        val real = this.real * complex.real - this.imaginary * complex.imaginary
        val imaginary = this.real * complex.imaginary + this.imaginary * complex.real
        return Complex(real, imaginary)
    }

}