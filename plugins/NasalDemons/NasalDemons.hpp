// PluginNasalDemons.hpp
// Gianluca Elia (elgiano@gmail.com)

#pragma once

#include "SC_PlugIn.hpp"

namespace NasalDemons {

class NasalDemons : public SCUnit {
public:
    NasalDemons();

    // Destructor
    // ~NasalDemons();

private:
    // Calc function
    void next(int nSamples);

    // Member variables
};

} // namespace NasalDemons
