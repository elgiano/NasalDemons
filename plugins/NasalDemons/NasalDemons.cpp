// PluginNasalDemons.cpp
// Gianluca Elia (elgiano@gmail.com)

#include "SC_PlugIn.hpp"
#include "NasalDemons.hpp"

static InterfaceTable* ft;

namespace NasalDemons {

NasalDemons::NasalDemons() {
    mCalcFunc = make_calc_function<NasalDemons, &NasalDemons::next>();
    next(1);
}

void NasalDemons::next(int nSamples) {
    const float* input = in(0);
    const float* gain = in(0);
    float* outbuf = out(0);

    // simple gain function
    for (int i = 0; i < nSamples; ++i) {
        outbuf[i] = input[i] * gain[i];
    }
}

} // namespace NasalDemons

PluginLoad(NasalDemonsUGens) {
    // Plugin magic
    ft = inTable;
    registerUnit<NasalDemons::NasalDemons>(ft, "NasalDemons");
}
